# AI Agent Guide

Guide for AI coding agents working on this repository. Read this before making changes.

## Project overview

Backend REST API for a **car rental system** (see `info.md` for the full feature map).

- **Language / runtime:** Java 21
- **Framework:** Spring Boot 4.0.8-SNAPSHOT (`spring-boot-starter-parent`)
- **Persistence:** Spring Data JPA + Hibernate, MySQL (`mysql-connector-j`, local dev runs on port `3308`, db `car_rental`)
- **Security:** Spring Security + JWT (`jjwt` 0.12.6) + BCrypt password hashing
- **Validation:** Jakarta Bean Validation (`spring-boot-starter-validation`)
- **API docs:** springdoc-openapi (Swagger UI at `/swagger-ui.html`)
- **Boilerplate:** Lombok
- **Build tool:** Maven — always use the wrapper (`./mvnw` / `mvnw.cmd`), not a system-wide `mvn`
- **Base package:** `com.example.spring_boot_project_api`

## Build, run, test

```bash
./mvnw clean compile        # compile
./mvnw spring-boot:run      # run locally (http://localhost:8080)
./mvnw test                 # run tests
./mvnw clean package        # build the jar
```

Database config lives in `src/main/resources/application.properties`
(`spring.datasource.*`, `jwt.secret`, `jwt.expiration`). DDL is `ddl-auto=update`.

## Folder structure

```
src/main/java/com/example/spring_boot_project_api/
├── SpringBootProjectApiApplication.java   # main entry point
├── config/               # SecurityConfig, JwtAuthFilter, OpenApiConfig
├── controller/           # @RestController — HTTP layer only, delegates to service
├── dto/
│   ├── request/<resource>/   # inbound payloads (e.g. VehicleRequestDTO)
│   └── response/<resource>/  # outbound payloads (e.g. VehicleResponseDTO)
├── enums/                # CarTypeEnum, FuelTypeEnum, RoleEnum, StatusEnum, ...
├── exception/            # GlobalExceptionHandler (@RestControllerAdvice)
├── mapper/               # model <-> DTO conversion helpers
├── model/                # @Entity JPA persistence models
├── repository/           # Spring Data JPA repositories
├── service/              # service interfaces
│   └── impl/             # @Service implementations
└── util/                 # JwtUtil and other stateless helpers
```

## Domain resources

Existing resources (each has model / repository / DTOs / service / controller):
`User`, `Vehicle`, `Location`, `Favorite`, `Notification`, `Services` (maintenance).

Key domain facts:

- Roles: `ADMIN > MANAGER > STAFF > CUSTOMER` (hierarchy defined in `SecurityConfig`).
  The first registered user becomes `ADMIN`.
- Auth: register/login under `/auth/**` are public; everything else requires a JWT.
  Tokens carry subject = email plus `id` and `role` claims.
- Vehicles use enums for type/transmission/fuel/status, persisted with
  `@Enumerated(EnumType.STRING)`; license plates must be unique.
- Entity tables follow the `tb_<name>` convention (e.g. `tb_vehicles`) via `@Table`.

## Conventions

- **Layering:** `controller` → `service` → `repository`. Controllers never touch
  entities or repositories directly; they work with DTOs and call service interfaces.
- **DTOs:** never expose JPA `model` classes over the API. Map between models and
  `dto/request` / `dto/response` types (per-resource sub-packages). Mapping is done
  manually in services or via the `mapper` package.
- **Services:** interface in `service/`, implementation annotated `@Service` in
  `service/impl/`.
- **Errors:** throw `RuntimeException` with a descriptive message from services;
  `GlobalExceptionHandler` maps it to HTTP 400. Don't catch-and-swallow in controllers.
- **Validation:** `jakarta.validation` annotations (`@NotBlank`, `@NotNull`, `@Size`,
  ...) on request DTOs/entities; controllers activate them with `@Valid @RequestBody`.
- **Entities:** Lombok `@Data`, `GenerationType.IDENTITY` ids, explicit `@Column`
  names, `@CreationTimestamp` / `@UpdateTimestamp` audit fields.
- **Injection:** constructor injection via Lombok `@RequiredArgsConstructor` is
  preferred; some existing classes use `@Autowired` field injection — don't mix
  styles within one class.
- **Authorization:** protect mutating/admin endpoints with `@PreAuthorize("hasRole('ADMIN')")`
  (or the appropriate role); method security is enabled globally.
- **Config:** environment-specific values belong in `application.properties`
  (or profile-specific files), never hardcoded in Java. Never commit real secrets.

## Notes for agents

- Don't add a new architectural layer or dependency unless the task actually needs it.
- Keep controller methods thin; business logic belongs in the service layer.
- When adding a new resource (e.g. `Booking`), create matching files across
  `model`, `enums` (if needed), `repository`, `dto/request/<resource>`,
  `dto/response/<resource>`, `mapper`, `service` (+ `impl`), and `controller` —
  don't skip the DTO/mapper layer "just this once."
- Follow existing naming: `<Resource>Controller`, `<Resource>Service`,
  `<Resource>ServiceImpl`, `<Resource>Repository`, `<Resource>RequestDTO`,
  `<Resource>ResponseDTO`.
- Run `./mvnw test` before considering a change complete.
