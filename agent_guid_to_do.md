# Agent Guide — Car Rental System (Backend + Frontend)

Guide for AI coding agents working on this repository. Read this before making changes.

This file covers the **whole project**: the Spring Boot REST API (`spring_backend/`) and the
Vue 3 frontend (`vue_frontend/`). It also lists recommended next steps at the bottom.

---

## 1. Repository layout

```
spring_backend/            # Backend REST API (Spring Boot + Maven)
vue_frontend/              # Frontend SPA (Vue 3 + Vite + Tailwind)
.github/                   # CI / hooks (modernize/java-upgrade)
```

---

## 2. Backend — Spring Boot REST API

### Tech stack

- **Language / runtime:** Java 21
- **Framework:** Spring Boot 4.0.8-SNAPSHOT (`spring-boot-starter-parent`)
- **Persistence:** Spring Data JPA + Hibernate, MySQL (`mysql-connector-j`, local dev on port `3309`, DB `car_rental`, running in Docker container `car-rental-mysql`)
- **Security:** Spring Security + JWT (`jjwt` 0.12.6) + BCrypt password hashing
- **Validation:** Jakarta Bean Validation (`spring-boot-starter-validation`)
- **API docs:** springdoc-openapi (Swagger UI at `/swagger-ui/index.html`)
- **Boilerplate:** Lombok
- **Mail:** Spring Mail (SMTP / Gmail) for password reset / notifications
- **Payments:** Bakong API integration
- **Uploads:** file.upload-dir (`uploads/`)
- **Build tool:** Maven — always use the wrapper (`./mvnw` / `mvnw.cmd`), not system `mvn`
- **Base package:** `com.example.spring_boot_project_api`

### Build, run, test

```bash
cd spring_backend
./mvnw clean compile        # compile
./mvnw spring-boot:run      # run locally (http://localhost:8080)
./mvnw test                 # run tests
./mvnw clean package        # build the jar
```

On Windows, use `mvnw.cmd` instead of `./mvnw` in PowerShell/CMD.

Database config lives in `src/main/resources/application.properties`. Values are read from
env vars (`DB_HOST`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, `JWT_EXPIRE`, `MAIL_USERNAME`,
`MAIL_PASSWORD`, `BAKONG_*`). See `.env.example`. **`.env` must be created locally from
`.env.example` — it is gitignored and never committed.** DDL is `ddl-auto=update`.

Local MySQL runs in Docker:
```bash
docker run --name car-rental-mysql -e MYSQL_ROOT_PASSWORD=<pw> -e MYSQL_DATABASE=car_rental -p 3309:3306 -d mysql:8.0
```

### Backend folder structure

```
spring_backend/src/main/java/com/example/spring_boot_project_api/
├── SpringBootProjectApiApplication.java   # main entry point
├── config/             # SecurityConfig, JwtAuthFilter, CorsConfig, OpenApiConfig, ...
│                       #   JacksonConfig, UploadConfig, AuthenticationEventListener
├── controller/         # @RestController — HTTP layer only, delegates to service
├── dto/
│   ├── request/<resource>/    # inbound validated payloads
│   └── response/<resource>/   # outbound payloads
├── enums/              # CarTypeEnum, FuelTypeEnum, RoleEnum, StatusEnum, RentalStatusEnum,
│                       #   ReservationStatusEnum, InspectionTypeEnum, DiscountTypeEnum,
│                       #   AuditActionEnum, ...
├── exception/          # GlobalExceptionHandler (@RestControllerAdvice)
├── mapper/             # model <-> DTO conversion helpers
├── model/              # @Entity JPA persistence models
├── repository/         # Spring Data JPA repositories
├── service/            # service interfaces
│   └── impl/           # @Service implementations
└── util/               # JwtUtil, ClientInfoUtil, and other stateless helpers
```

### Domain resources (backend)

Each resource has model / repository / DTOs / service (+ impl) / controller:

- **Auth / User:** `User`, `PasswordResetToken`, `LoginHistory`
- **Vehicle:** `Vehicle`, `VehicleImage`
- **Booking:** `Reservation`, `ReservationServices`, `Rental`, `RentalDocument`
- **Payments:** `Invoice`, `Discount`, `DiscountUsage`, Bakong integration
- **Maintenance:** `MaintenanceRecord`
- **Audit:** `AuditLog` (tracks CREATE/UPDATE/DELETE actions with actor, entity, old/new values) ✅ **done**
- **Other:** `Location`, `Favorite`, `Review`, `Notification`, `Services`
  (maintenance services), `Attachment`, `Inspection`

Key domain facts:

- Roles: `ADMIN > MANAGER > STAFF > CUSTOMER` (hierarchy in `SecurityConfig`). First registered
  user becomes `ADMIN`; subsequent registrations default to `CUSTOMER`.
- Auth: register/login under `/api/auth/**` are public; everything else requires a JWT. Tokens
  carry subject = email plus `id` and `role` claims. `/api/auth/register` and `/api/auth/login`
  both return the JWT directly in the response body (`token` field) — no separate login call
  needed after registering.
- Vehicles use enums for type / transmission / fuel / status persisted with
  `@Enumerated(EnumType.STRING)`; license plates are unique.
- Entity tables follow the `tb_<name>` convention (e.g. `tb_vehicles`) via `@Table`.
- Rental lifecycle: `Pending → Confirmed → Picked Up → Active Rental → Returned → Completed`.
- Payments include deposit, insurance, additional services, discount, invoice, and Bakong checks.
- Admin-only endpoints (e.g. `/api/admin/audit-logs`, `/api/admin/login-history`) are gated with
  `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")` and require `@EnableMethodSecurity` on
  `SecurityConfig` — confirmed working via Swagger UI.

### Backend conventions

- **Layering:** `controller` → `service` → `repository`. Controllers never touch entities or
  repositories directly; they work with DTOs and call service interfaces.
- **DTOs:** never expose JPA `model` classes over the API. Map between models and
  `dto/request` / `dto/response` types (per-resource sub-packages). Mapping is done manually in
  services or via the `mapper` package.
- **Services:** interface in `service/`, implementation annotated `@Service` in `service/impl/`.
- **Errors:** throw `RuntimeException` with a descriptive message from services;
  `GlobalExceptionHandler` maps it to HTTP 400. Don't catch-and-swallow in controllers.
- **Validation:** `jakarta.validation` annotations on request DTOs/entities; controllers
  activate them with `@Valid @RequestBody`.
- **Entities:** Lombok `@Data`, `GenerationType.IDENTITY` ids, explicit `@Column` names,
  `@CreationTimestamp` / `@UpdateTimestamp` audit fields.
- **Injection:** constructor injection via Lombok `@RequiredArgsConstructor` is preferred; don't
  mix styles within one class.
- **Authorization:** protect mutating/admin endpoints with `@PreAuthorize("hasRole('ADMIN')")`
  (or the appropriate role); method security is enabled globally via `@EnableMethodSecurity`.
- **Config:** environment-specific values belong in `application.properties`, never hardcoded in
  Java. Never commit real secrets (use env vars via `.env`, gitignored).
- **pom.xml:** double-check artifact IDs against the real Spring Boot BOM before adding a
  dependency by hand — invalid/duplicate artifact IDs silently break the entire dependency
  resolution and produce misleading "missing classpath" errors across unrelated files.
- **Swagger auth:** `OpenApiConfig` defines a `bearerAuth` HTTP/Bearer security scheme. To test
  protected endpoints in Swagger UI: register or log in, copy the `token` value from the response
  body (not the whole JSON, not the field label), click **Authorize**, paste just the token, then
  **Authorize → Close**.

---

## 3. Frontend — Vue 3 SPA

### Tech stack

- **Framework:** Vue 3 (`<script setup>` style) + Composition API primitives (reactive store)
- **Build:** Vite 8 (`@vitejs/plugin-vue`)
- **Styling:** Tailwind CSS 4 via `@tailwindcss/vite` (fonts via Google Fonts in `style.css`)
- **Routing:** Vue Router (history mode) — `@` alias → `src/`
- **HTTP:** Axios (central `services/api.js`, `baseURL: "/api"`)
- **State:** lightweight reactive store (`stores/auth.store.js`), not Pinia yet
- **Package manager:** npm

### Frontend commands

```bash
cd vue_frontend
npm install       # install deps
npm run dev       # dev server (Vite)
npm run build     # production build
npm run preview   # preview the build
```

### Frontend folder structure

```
vue_frontend/src/
├── App.vue
├── main.js               # createApp + router
├── style.css             # global styles + Tailwind + Google Fonts
├── assets/               # static images/svg
├── components/
│   ├── base/             # BaseButton.vue, BaseInput.vue (reusable primitives)
│   ├── layout/           # (empty)
│   └── ui/               # (empty)
├── composables/          # useFetch.js
├── layouts/              # FrontLayout.vue, BackLayout.vue
├── modules/              # recommended feature-based architecture (see modules/NOTE.md)
├── pages/
│   ├── auth/             # Login.vue, Register.vue
│   ├── dashboard/        # Dashboard.vue
│   ├── home/             # Home.vue, home/Card.vue
│   ├── preview/          # Preview.vue, components/ (LeftPannel, RightPannel, PreText)
│   └── NotFound.vue
├── router/               # index.js (routes)
├── services/             # api.js (Axios instance)
└── stores/               # auth.store.js (reactive store)
```

### Frontend routes (router/index.js)

| Path          | Component   | Notes                                    |
| ------------- | ----------- | ---------------------------------------- |
| `/`           | —           | redirects to `/preview` (demo)           |
| `/preview`    | Preview     | landing/demo screen                      |
| `/dashboard`  | Dashboard   | main app (auth guard not yet added)      |
| `/login`      | Login       |                                          |
| `/register`   | Register    |                                          |
| `/home`       | Home        | real landing page (currently secondary)  |
| `/:pathMatch` | NotFound    | 404 catch-all                            |

### Frontend conventions

- Group related UI by **feature/module** (see `modules/NOTE.md`) as the app grows; avoid dumping
  everything under flat `pages/`, `services/`, `stores/` folders.
- Reuse `components/base/*` for shared input/button primitives; keep domain components local to
  their page/feature.
- Centralize HTTP calls through `services/api.js`; add Axios interceptors here for auth tokens
  and error handling (not yet implemented).
- All network calls go through `/api` base URL — wire up a Vite proxy to `http://localhost:8080`
  so the dev server can reach the backend.
- Follow Vue 3 `<script setup>` Composition API style used across existing files.
- Backend auth responses return `{ id, email, role, token }` — store `token` and `role` in
  `auth.store.js`; attach `Authorization: Bearer <token>` on every request once wired up.

---

## 4. "To do next" / Recommended next steps

### Backend (API completeness)

- [x] **AuditLog feature:** model, repository, service (+impl), controller, `AuditActionEnum`,
      response DTO — implemented, `@PreAuthorize`-protected, verified `200 OK` via Swagger UI,
      merged into `test`.
- [x] **pom.xml cleanup:** removed duplicate `spring-boot-starter-validation` / `lombok`
      declarations and invalid artifact IDs (`spring-boot-starter-webmvc`,
      `spring-boot-starter-data-jpa-test`, `spring-boot-starter-validation-test`).
- [x] **Method security:** confirmed `@EnableMethodSecurity` on `SecurityConfig` so
      `@PreAuthorize` is enforced.
- [ ] **CORS / security audit:** confirm `CorsConfig` allows the Vue dev origin and that JWT
      routes and role guards are enforced on all protected endpoints.
- [ ] **Unify error responses:** make `GlobalExceptionHandler` return consistent JSON
      (`{ status, message, ... }`) and add HTTP status codes beyond 400 (403, 404, 500).
- [ ] **Repositories/service coverage:** verify every `model` has a full set of endpoints
      (CRUD + list + search/filter), especially `Rental`, `Reservation`, and filtering) and every
      service has a corresponding unit test.
- [ ] **Tests:** add `@WebMvcTest` / `@DataJpaTest` / Mockito service tests under
      `src/test/java` (currently minimal). Cover reservation/rental price math, discount logic,
      and Bakong flow.
- [ ] **Seeding / demo data:** provide a way to seed sample vehicles, locations, customers, and
      admin user for development.
- [ ] **Reports/analytics endpoints:** revenue, rental, vehicle-utilization, and monthly
      summaries (see `info.md` §11) are listed but not yet implemented.
- [ ] **Notifications:** hook up real notification delivery (email / DB records) for booking
      confirmed/cancelled, payment, and rental reminders.
- [ ] **API docs polish:** ensure DTO examples and auth annotations render well in Swagger.
- [ ] **Wire AuditLog writes:** call `auditLogService.log(...)` from mutating service methods
      (create/update/delete on Vehicle, Reservation, Rental, User, etc.) so the audit trail
      actually fills up — currently the entity/endpoints exist but nothing writes to it yet.

### Frontend (wiring the real app) — **current focus**

- [ ] **Vite proxy:** add `server.proxy` in `vite.config.js` so `/api` redirects to
      `http://localhost:8080`, then make `/login` and `/register` actually call the backend.
- [ ] **Auth flow:** implement real login/register against the API, store JWT + role in
      `auth.store.js`, add a `router.beforeEach` guard, and wire the Axios `Authorization`
      header in `services/api.js`.
- [ ] **Redirect:** change `/` from `/preview` to the real `Home` (or `/dashboard`) for
      production.
- [ ] **State management:** decide between Pinia and the existing reactive store as features grow.
- [ ] **Feature pages:** build out vehicle listing/search/filter, vehicle detail, reservation,
      rental history, dashboard stats, and admin CRUD screens using the backend endpoints.
- [ ] **Reusable UI:** fill `components/ui/` and `components/layout/`; adopt the
      feature/module structure from `modules/NOTE.md`.
- [ ] **Layouts:** use `FrontLayout.vue` (public pages) and `BackLayout.vue` (authenticated /
      admin pages) consistently with route-level `layout` wrap.

### Cross-cutting

- [ ] `AGENTS.md`-style pointer: keep backend `agent_guide_ai.md` / `agent_guide.md` in sync, and
      consider mirroring this root guide into the frontend so agents land in the right place.
- [ ] Environment docs: finalize `.env.example` with real keys and document how to run both
      servers together for local development.
- [ ] Deployment: verify Spring profile for production (currently no profile-specific config) and
      Vercel SPA rewrite (`vercel.json`) points at the correct API origin.

---

## Notes for agents

- Don't add a new architectural layer or dependency unless the task actually needs it.
- Keep controller methods thin; business logic belongs in the service layer.
- When adding a new resource (e.g. `Booking`), create matching files across `model`, `enums`
  (if needed), `repository`, `dto/request/<resource>`, `dto/response/<resource>`, `mapper`,
  `service` (+ `impl`), and `controller` — don't skip the DTO/mapper layer.
- Follow existing naming: `<Resource>Controller`, `<Resource>Service`, `<Resource>ServiceImpl`,
  `<Resource>Repository`, `<Resource>RequestDTO`, `<Resource>ResponseDTO`.
- Backend: run `./mvnw test` before considering a change complete.
- Frontend: run `npm run build` (or at least `npm run dev`) to verify changes compile.