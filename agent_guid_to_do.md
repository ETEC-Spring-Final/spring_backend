# Agent Guide — Car Rental System (Backend + Frontend)

Guide for AI coding agents working on this repository. Read this before making changes.

This file is the **single source of truth** and covers the whole project: the Spring Boot REST
API (`spring_backend/`) and the Vue 3 frontend (`vue_frontend/`). It is kept in sync with the
current code. The frontend also has a short pointer file at `vue_frontend/AGENTS.md`.

> **Last verified:** 2026-09-18 against every `@RestController`. If you add/remove an endpoint,
> update the API tables in §2.6 below.

---

## 1. Repository layout

```
spring_backend/            # Backend REST API (Spring Boot 4 + Maven)
vue_frontend/              # Frontend SPA (Vue 3 + Vite + Tailwind)
.github/                   # CI / hooks
```

---

## 2. Backend — Spring Boot REST API

### 2.1 Tech stack

- **Language / runtime:** Java 21
- **Framework:** Spring Boot 4.0.8-SNAPSHOT (`spring-boot-starter-parent`)
- **Persistence:** Spring Data JPA + Hibernate, MySQL 8 (`mysql-connector-j`). Local dev DB is
  `car_rental` on port `3309`, in Docker container `car-rental-mysql`. DDL is `ddl-auto=update`.
- **Security:** Spring Security + JWT (`jjwt` 0.12.6) + BCrypt. Google & Facebook OAuth2 login.
- **Validation:** Jakarta Bean Validation (`spring-boot-starter-validation`)
- **API docs:** springdoc-openapi — Swagger UI at `/swagger-ui/index.html`
- **Boilerplate:** Lombok
- **Mail:** Spring Mail (SMTP/Gmail) for password reset / notifications
- **Payments:** Bakong KHQR integration (`bakong-khqr` SDK)
- **Uploads:** `file.upload-dir=uploads`; the active flow is **Cloudinary-style**: clients POST a
  `fileUrl` to `/api/attachments` (JSON) — local multipart upload is deprecated (images 404).
- **Build tool:** Maven — always use the wrapper (`./mvnw` / `mvnw.cmd`), never system `mvn`
- **Base package:** `com.example.spring_boot_project_api`

### 2.2 Build, run, test

```bash
cd spring_backend
./mvnw clean compile        # compile
./mvnw spring-boot:run      # run locally (http://localhost:8080)
./mvnw test                 # run tests
./mvnw clean package        # build the jar
```

On Windows use `mvnw.cmd`. **Never run a production build against a local DB.**

Config lives in `src/main/resources/application.properties`; all secrets come from env vars
(`DB_HOST`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, `JWT_EXPIRE`, `MAIL_USERNAME`, `MAIL_PASSWORD`,
`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `FACEBOOK_CLIENT_ID`, `FACEBOOK_CLIENT_SECRET`,
`BAKONG_ACCOUNT_ID`, `BAKONG_BASE_URL`, `EMAIL`). `.env` is gitignored; create it from
`.env.example`. **Never commit real secrets.**

Local MySQL in Docker:

```bash
docker run --name car-rental-mysql -e MYSQL_ROOT_PASSWORD=<pw> -e MYSQL_DATABASE=car_rental -p 3309:3306 -d mysql:8.0
```

### 2.3 Backend folder structure

```
spring_backend/src/main/java/com/example/spring_boot_project_api/
├── SpringBootProjectApiApplication.java   # main entry point
├── config/
│   ├── SecurityConfig.java                # filter chain, public routes, CORS source, OAuth2 login
│   ├── JwtAuthFilter.java                 # parses Bearer token, sets SecurityContext
│   ├── CorsConfig.java                    # WebMvcConfigurer (allow-all origins, dev only)
│   ├── OpenApiConfig.java                 # Swagger bearerAuth scheme
│   ├── JacksonConfig.java                 # (de)serialization tweaks
│   ├── UploadConfig.java                  # multipart/file-dir beans
│   ├── AuthenticationEventListener.java   # writes LoginHistory on success/failure
│   ├── CustomOAuth2UserService.java       # OAuth2 user load (Google/Facebook)
│   ├── CustomOAuth2User.java
│   └── OAuth2AuthenticationSuccessHandler.java   # redirects to /oauth2/redirect?token=...
├── controller/         # @RestController — HTTP layer only
├── dto/
│   ├── request/<resource>/    # inbound, validated payloads
│   └── response/<resource>/   # outbound payloads
├── enums/              # CarType, FuelType, Status, Role, Transmission, RentalStatus,
│                       # ReservationStatus, InspectionType, DiscountType, AuditAction,
│                       # FuelLevel, DocumentType, Gender, InvoiceStatus, MaintenanceStatus,
│                       # MaintenanceType, NotificationType, AuthProvider
├── exception/          # GlobalExceptionHandler (@RestControllerAdvice)
├── mapper/             # model <-> DTO helpers (mostly unused — manual mapping in services)
├── model/              # @Entity JPA models
├── repository/         # Spring Data JPA repositories
├── service/            # interfaces
│   └── impl/           # @Service implementations
└── util/               # JwtUtil, ClientInfoUtil
```

### 2.4 Domain resources

Each resource has model / repository / DTOs / service (+impl) / controller where applicable:

- **Auth & users:** `User`, `PasswordResetToken`, `LoginHistory`
- **Catalog:** `Vehicle`, `VehicleImage`, `Brand`
- **Booking:** `Reservation`, `ReservationServices`, `Rental`, `RentalDocument`
- **Payments:** `Invoice`, `Discount`, `DiscountUsage`, Bakong
- **Service ops:** `MaintenanceRecord`, `Inspection`, `Services`
- **Customer:** `Favorite`, `Review`, `Notification`
- **Platform:** `SiteSettings`, `Attachment`, `AuditLog`

Key domain facts:

- Roles: `ADMIN > MANAGER > STAFF > CUSTOMER`. First registered user becomes `ADMIN`; later
  registrations default to `CUSTOMER`. Method security auto-suffixes roles with `ROLE_`.
- **Auth:** `/api/auth/register` and `/api/auth/login` are public and return the JWT directly
  in the body (`token`). Tokens carry subject = email + `id` + `role` claims. Every other route
  requires a JWT unless explicitly permitted in `SecurityConfig`.
- **SecurityConfig public allow-list** (keep in sync — the frontend route guards mirror this):
  - `OPTIONS /**`
  - `/api/v1/bakong/**`
  - `/api/auth/**` (URL-level only; most methods are `@PreAuthorize`-gated)
  - `/oauth2/**`, `/login/oauth2/**`
  - Swagger docs
  - `GET /api/vehicles/**`, `GET /api/locations/**`, `GET /api/reviews/vehicle/**`,
    `GET /api/reviews/*` (but `GET /api/reviews/my-reviews` is JWT), `GET /api/settings`
  - Everything else → `.anyRequest().authenticated()`.
- Entity tables use the `tb_<name>` convention (`@Table`).
- Rental lifecycle: `PENDING → CONFIRMED → PICKED_UP → ACTIVE_RENTAL → RETURNED → COMPLETED`.
- Inventory statuses (`StatusEnum`): `AVAILABLE / RESERVED / RENTED / MAINTENANCE / UNAVAILABLE`.

### 2.5 Backend conventions

- **Layering:** `controller` → `service` → `repository`. Controllers never touch entities or
  repositories; they use DTOs and service interfaces.
- **DTOs:** never expose JPA `model` classes over the API. Map in the service (or `mapper`).
- **Services:** interface in `service/`, `@Service` implementation in `service/impl/`.
- **Errors:** throw `RuntimeException` with a clear message; `GlobalExceptionHandler` maps to
  HTTP 400. `@PreAuthorize` denial → 403, auth failure → 401.
- **Validation:** `jakarta.validation` on request DTOs + `@Valid @RequestBody`.
- **Entities:** Lombok `@Data`, `GenerationType.IDENTITY`, explicit `@Column`, 
  `@CreationTimestamp` / `@UpdateTimestamp`.
- **Injection:** constructor injection via Lombok `@RequiredArgsConstructor`; don't mix with
  `@Autowired` field injection in the same class.
- **Current user:** prefer `@AuthenticationPrincipal CustomUserDetails` (see `NotificationController`)
  over `SecurityContextHolder`. `CustomUserDetails.getId()` is the user id.
- **Authorization:** `@PreAuthorize("hasAnyRole('ADMIN','MANAGER'|...)")` on controllers;
  `@EnableMethodSecurity` is on.
- **pom.xml:** double-check artifact IDs against the real Spring Boot BOM before hand-adding a
  dependency — an invalid/duplicate ID silently breaks dependency resolution.
- **Swagger:** authorize with the raw `token` string (not the whole JSON).

### 2.6 Backend API reference (complete, verified 2026-09)

Auth = the minimum to call the endpoint. "JWT" = any authenticated user. `@PreAuthorize` roles
are shown in the Auth column. `?page&size` params are Spring `Pageable`.

#### Auth — `/api/auth` (base URL is under `/api/auth/**`, permitted at URL level)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register, returns `AuthResponseDTO` with `token` |
| POST | `/api/auth/login` | Public | Login, returns `AuthResponseDTO` with `token` |
| POST | `/api/auth/logout` | JWT | Records logout in LoginHistory |
| POST | `/api/auth/forgot-password` | Public | Sends reset email |
| POST | `/api/auth/reset-password` | Public | Resets password with token |
| GET | `/api/auth/users` | ADMIN, MANAGER | List users (see **dup** note) |
| GET | `/api/auth/users/{id}` | ADMIN, MANAGER | Get user |
| PATCH | `/api/auth/users/{id}/role?role=X` | ADMIN | Change role (`RoleEnum`) |
| PATCH | `/api/auth/users/{id}/active?active=X` | ADMIN, MANAGER | Activate/deactivate |
| DELETE | `/api/auth/users/{id}` | ADMIN | Delete user |

> **Duplicate surface:** the same user-management endpoints also exist under `/api/users`
> (ADMIN-only) in `UserManagementController`. Frontend uses `/api/auth/users` in
> `CustomerManagement.vue` and `/api/users` in `NotificationManagement.vue`. Prefer one —
> recommend consolidating on `/api/auth/users` (or `/api/admin/users`) and deleting the other.

#### User management — `/api/users` (ADMIN only, duplicates `/api/auth/users`)

| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/users` | ADMIN |
| GET | `/api/users/{id}` | ADMIN |
| PATCH | `/api/users/{id}/role?role=X` | ADMIN |
| PATCH | `/api/users/{id}/active?active=X` | ADMIN |
| DELETE | `/api/users/{id}` | ADMIN |

#### My profile — `/api/user-profiles` (JWT)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/user-profiles/me` | JWT | Full profile (`firstName`,`lastName`,`phone`,`profilePicture`,…) |
| PUT | `/api/user-profiles/me` | JWT | Update profile |
| POST | `/api/user-profiles/me/change-password` | JWT | Change password |
| GET | `/api/user-profiles/me/login-history` | JWT | My login history, paged (default size 8, `loggedInAt` desc) |

#### Vehicles — `/api/vehicles`

| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/vehicles` | Public |
| GET | `/api/vehicles/{id}` | Public |
| POST | `/api/vehicles` | ADMIN |
| PUT | `/api/vehicles/{id}` | ADMIN |
| DELETE | `/api/vehicles/{id}` | ADMIN |

`VehicleResponseDTO`: `id, brandId, brandName, model, yearOfManufacture, licensePlate, color,
type (CarTypeEnum), transmission, fuelType, seats, doors, luggages, pricePerDay (BigDecimal),
mileAge, description, status (StatusEnum), createdAt, updatedAt`.

> **No server-side search/filter yet** — Explore/Home filter client-side. Add
> `?q=&type=&brandId=&minPrice=&maxPrice=&status=` to `GET /api/vehicles` to scale.

#### Vehicle images — `/api/vehicle-images`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| GET | `/api/vehicle-images/{vehicleId}` | **JWT in practice** | Comment says "public" but it is NOT in the `SecurityConfig` allow-list — add `GET /api/vehicle-images/**` to permitAll if the detail page should work logged-out |
| GET | `/api/vehicle-images` | JWT | All images |
| POST | `/api/vehicle-images` | ADMIN, MANAGER | JSON `{vehicleId, attachmentId}` |
| PUT | `/api/vehicle-images/{id}` | ADMIN, MANAGER | JSON |
| DELETE | `/api/vehicle-images/{id}` | ADMIN, MANAGER | |

2-step image flow used by `VehicleManagement.vue`:
1. `POST /api/attachments { fileUrl, documentType: "VEHICLE_IMAGE", isPrimary, displayOrder }`
2. `POST /api/vehicle-images { vehicleId, attachmentId }`.

Local multipart upload is **deprecated** and commented out (files were written to disk but never
served → 404 grey-box bug). Use the attachment JSON flow.

#### Brands — `/api/brands`

| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/brands` | JWT (not in public allow-list) |
| GET | `/api/brands/{id}` | JWT |
| POST | `/api/brands` | ADMIN, MANAGER, STAFF |
| PUT | `/api/brands/{id}` | ADMIN, MANAGER, STAFF |
| DELETE | `/api/brands/{id}` | ADMIN, MANAGER, STAFF |

#### Locations — `/api/locations`

| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/locations` | Public |
| GET | `/api/locations/{id}` | Public |
| POST | `/api/locations` | ADMIN, MANAGER, STAFF |
| PUT | `/api/locations/{id}` | ADMIN, MANAGER, STAFF |
| DELETE | `/api/locations/{id}` | ADMIN, MANAGER, STAFF |

#### Reservations — `/api/reservations`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/reservations` | JWT | Create. Overlap-guarded. **For CUSTOMER role also auto-creates Rental (PENDING) + Invoice (UNPAID) in one call**; fires BOOKING_CONFIRMED notification. Request: `pickUpLocationId, returnLocationId, pickUpDateTime, returnDateTime, serviceIds[], discountCode, notes` |
| GET | `/api/reservations/{id}` | JWT | Owner-scoped in service |
| GET | `/api/reservations/my-reservations` | JWT | |
| GET | `/api/reservations` | ADMIN, MANAGER, STAFF | |
| PATCH | `/api/reservations/{id}/status?status=X` | ADMIN, MANAGER, STAFF | `ReservationStatusEnum`; →CONFIRMED fires BOOKING_CONFIRMED |
| PATCH | `/api/reservations/{id}/cancel` | JWT | Owner. **PENDING-only**; cancels linked UNPAID invoice; fires BOOKING_CANCELLED |
| PUT | `/api/reservations/{id}` | ADMIN, MANAGER, STAFF | Recomputes server-side totals |
| DELETE | `/api/reservations/{id}` | ADMIN, MANAGER, STAFF | |

`ReservationRequestDTO`: `vehicleId, pickUpLocationId, returnLocationId, pickUpDateTime (LocalDateTime),
returnDateTime (LocalDateTime), depositAmount, discountAmount, additionalCharges, discountCode,
serviceIds (List<Long>), notes`.

`ReservationResponseDTO`: `id, userId, vehicleId, pickUpLocationId, returnLocationId,
pickUpDateTime, returnDateTime, status, totalPrice, depositAmount, discountAmount,
additionalCharges, **rentalId, invoiceId** (nullable — populated for CUSTOMER bookings),
notes, createdAt, updatedAt`.

> Server-side totals: base price = `pricePerDay × days`; only active services added;
> subtotal = base + services; discount via `DiscountService.applyDiscount(code, subtotal)`
> (PERCENTAGE → HALF_UP 2dp; FIXED_AMOUNT → min(value, subtotal)); total = subtotal − discount.

#### Reservation services (add-ons per reservation) — `/api/reservation-services`

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/reservation-services` | JWT |
| GET | `/api/reservation-services/{id}` | JWT |
| GET | `/api/reservation-services/my-reservation-services` | JWT |
| GET | `/api/reservation-services` | ADMIN, MANAGER, STAFF |
| PUT | `/api/reservation-services/{id}` | JWT (no `@PreAuthorize` — consider owner/role check) |
| DELETE | `/api/reservation-services/{id}` | JWT (ditto) |

#### Rentals — `/api/rentals`

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/rentals` | ADMIN, MANAGER, STAFF |
| GET | `/api/rentals/{id}` | JWT (owner-scoped in service) |
| GET | `/api/rentals/my-rentals` | JWT |
| GET | `/api/rentals` | ADMIN, MANAGER, STAFF |
| PUT | `/api/rentals/{id}` | ADMIN, MANAGER, STAFF |
| PATCH | `/api/rentals/{id}/status?status=X` | ADMIN, MANAGER, STAFF |
| DELETE | `/api/rentals/{id}` | ADMIN, MANAGER, STAFF |

`RentalResponseDTO`: `id, reservationId, vehicleId, userId, pickUpLocationId, returnLocationId,
pickUpDateTime, expectedReturnDateTime, actualReturnDateTime, status, basePrice, discountAmount,
additionalCharges, lateFee, totalPrice, notes, createdAt, updatedAt`.

#### Rental documents — `/api/rental-documents`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/rental-documents` | ADMIN, MANAGER, STAFF | JSON `{rentalId, attachmentId}` |
| GET | `/api/rental-documents/my-rental-document` | JWT | Owner's docs |
| GET | `/api/rental-documents/{id}` | ADMIN, MANAGER, STAFF | |
| GET | `/api/rental-documents/rental/{rentalId}` | ADMIN, MANAGER, STAFF | |
| GET | `/api/rental-documents` | ADMIN, MANAGER, STAFF | |
| PATCH | `/api/rental-documents/{id}?documentType=X` | ADMIN, MANAGER, STAFF | |
| DELETE | `/api/rental-documents/{id}` | ADMIN, MANAGER, STAFF | |

> Multi-part upload (`POST /api/rental-documents/{rentalId}/upload`) was **deprecated/removed**,
> but `vue_frontend/src/services/rentals.js` still calls it. Same 2-step attachment flow as
> vehicle images. Fix the frontend before relying on doc upload.

#### Favorites — `/api/favorites` (JWT)

| Method | Endpoint |
|--------|----------|
| POST | `/api/favorites/{vehicleId}` |
| GET | `/api/favorites` |
| DELETE | `/api/favorites/{vehicleId}` |

#### Reviews — `/api/reviews`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/reviews` | JWT | |
| PUT | `/api/reviews/{id}` | JWT | Owner |
| DELETE | `/api/reviews/{id}` | JWT | Owner |
| PATCH | `/api/reviews/{id}/visibility` | ADMIN, MANAGER, STAFF | Moderation (hide/show) `{isVisible}` |
| GET | `/api/reviews/{id}` | Public | |
| GET | `/api/reviews/vehicle/{vehicleId}` | Public | Paged (default size 8) |
| GET | `/api/reviews/vehicle/{vehicleId}/count?rating=X` | Public | Count for a rating |
| GET | `/api/reviews/my-reviews` | JWT | Paged |
| GET | `/api/reviews` | ADMIN, MANAGER, STAFF | Paged (all reviews) |

#### Invoices — `/api/invoices`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/invoices` | ADMIN, MANAGER, STAFF | Manual create (admin flow) |
| GET | `/api/invoices/{id}` | JWT | Owner-scoped |
| GET | `/api/invoices/my-invoices` | JWT | |
| GET | `/api/invoices` | ADMIN, MANAGER, STAFF | |
| POST | `/api/invoices/{id}/confirm-payment` | JWT | Owner-only, idempotent when PAID, rejects CANCELLED. Body `{md5}` (`InvoicePaymentConfirmDTO`). Verifies via `bakongService.checkTransactionByMD5`, marks PAID, fires PAYMENT_SUCCESS |
| PUT | `/api/invoices/{id}` | ADMIN, MANAGER, STAFF | |
| DELETE | `/api/invoices/{id}` | ADMIN, MANAGER, STAFF | |

`InvoiceResponseDTO`: `id, rentalId, invoiceNumber, issueDate, dueDate, subtotal, discountAmount,
taxAmount, lateFee, totalAmount, status (InvoiceStatusEnum), createdAt`.

> **Auto-creation:** `POST /api/reservations` (CUSTOMER role) creates the Rental + Invoice and
> returns their ids on `ReservationResponseDTO.rentalId/.invoiceId` — no separate invoice step
> needed for the public booking flow.

#### Discounts — `/api/discounts`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/discounts` | ADMIN, MANAGER | |
| GET | `/api/discounts` | ADMIN, MANAGER | Full admin list |
| GET | `/api/discounts/active` | Any signed-in user | Currently-redeemable codes (expiry/usage filtered server-side); declared before `/{id}` |
| GET | `/api/discounts/{id}` | ADMIN, MANAGER | |
| PUT | `/api/discounts/{id}` | ADMIN, MANAGER | |
| DELETE | `/api/discounts/{id}` | ADMIN | |

> `DiscountResponseDTO`: `id, code, description, type (PERCENTAGE|FIXED_AMOUNT), value, validFrom,
> validTo, maxUses, usedCount, isActive`. `usedCount` increments when a code is applied
> (`DiscountServiceImpl.applyDiscount`).

#### Discount usage — `/api/discount-usages`

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/discount-usages` | JWT |
| GET | `/api/discount-usages/{id}` | JWT |
| GET | `/api/discount-usages/my-discount-usages` | JWT |
| GET | `/api/discount-usages` | ADMIN, MANAGER, STAFF |
| PUT | `/api/discount-usages/{id}` | ADMIN, MANAGER, STAFF |
| DELETE | `/api/discount-usages/{id}` | ADMIN, MANAGER, STAFF |

#### Notifications — `/api/notifications`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| POST | `/api/notifications/{userId}/notify` | ADMIN, MANAGER, STAFF | |
| GET | `/api/notifications/me/inbox` | JWT | Owner |
| GET | `/api/notifications/me/unread-count` | JWT | Badge count |
| PATCH | `/api/notifications/{id}/read` | JWT | Owner |
| PATCH | `/api/notifications/me/read-all` | JWT | Owner |
| DELETE | `/api/notifications/{id}` | JWT | Owner |
| GET | `/api/notifications` | ADMIN, MANAGER, STAFF | All |

`NotificationResponseDTO`: `id, userId, userEmail, type (NotificationTypeEnum), title, message,
isRead, createdAt`.

#### Inspections — `/api/inspections` (class-level `ADMIN, MANAGER, STAFF`)

| Method | Endpoint |
|--------|----------|
| POST | `/api/inspections` |
| PUT | `/api/inspections/{id}` |
| DELETE | `/api/inspections/{id}` |
| GET | `/api/inspections/{id}` |
| GET | `/api/inspections/rental/{rentalId}` |
| GET | `/api/inspections/type?type=X` | paged |
| GET | `/api/inspections` | paged |

#### Maintenance — `/api/maintenance-records`

| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/maintenance-records` | ADMIN, MANAGER, STAFF |
| GET | `/api/maintenance-records/{id}` | JWT (no public allow-list entry) |
| GET | `/api/maintenance-records` | JWT |
| PUT | `/api/maintenance-records/{id}` | ADMIN, MANAGER, STAFF |
| DELETE | `/api/maintenance-records/{id}` | ADMIN, MANAGER |

#### Services (add-on / maintenance services catalog) — `/api/services`

| Method | Endpoint | Auth | ⚠ Note |
|--------|----------|------|--------|
| POST | `/api/services` | JWT (no `@PreAuthorize`) | Any logged-in user can create — **gate it** |
| PUT | `/api/services/{id}` | JWT (no `@PreAuthorize`) | Same |
| DELETE | `/api/services/{id}` | JWT (no `@PreAuthorize`) | Same |
| GET | `/api/services` | JWT | Public list intended — add permitAll or a `?active=true` read endpoint |
| GET | `/api/services/{id}` | JWT | Same |

> The customer reservation form calls `GET /api/services` → customers get 403. Make the catalog
> readable publicly and restrict writes to staff roles.

#### Attachments — `/api/attachments` (ADMIN, MANAGER, STAFF)

| Method | Endpoint | Notes |
|--------|----------|-------|
| POST | `/api/attachments` | JSON `{fileUrl, documentType, isPrimary, displayOrder}` |
| POST | `/api/attachments/upload` | Multipart (deprecated on disk; do not rely on it) |
| GET | `/api/attachments/{id}` | |
| GET | `/api/attachments` | |
| PUT | `/api/attachments/{id}` | |
| DELETE | `/api/attachments/{id}` | |

#### Site settings — `/api/settings`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| GET | `/api/settings` | Public | Site branding + contact (`SiteSettingsResponseDTO`: `id, siteName, logoUrl, faviconUrl, contactEmail, contactPhone, address, facebookUrl, telegramUrl, updatedAt`) |
| PUT | `/api/settings` | ADMIN, MANAGER | Update branding |

#### Admin — `/api/admin`

| Method | Endpoint | Auth | Notes |
|--------|----------|------|-------|
| GET | `/api/admin/audit-logs` | ADMIN, MANAGER | Filters `entityName`, `userId`, `action`; paged |
| GET | `/api/admin/audit-logs/{entityName}/{entityId}` | ADMIN, MANAGER | Paged |
| GET | `/api/admin/login-history?email=X` | ADMIN, MANAGER | Paged |

#### Bakong payments — `/api/v1/bakong` (Public — `@CrossOrigin` at controller)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/bakong/generate-qr` | Body `BakongRequest` → `KHQRResponse<KHQRData>` = `{KHQRStatus:{code,errorCode,message}, data:{qr, md5, ...}}` |
| POST | `/api/v1/bakong/qr-image` | Body `KHQRData` (>=`{qr}`) → PNG bytes (`image/png`, consume as blob) |
| POST | `/api/v1/bakong/check-transaction` | Body `CheckTransactionRequest {md5}` → `BakongResponse {responseCode, responseMessage, errorCode, data}`; success = `responseCode === 0` |

> `BakongRequest` fields (all optional with defaults): `currency (KHR|USD), amount, merchantName,
> merchantCity, merchantId, acquiringBank, upiAccountInformation, expirationTimestamp,
> billNumber, storeLabel, terminalLabel, mobileNumber, purposeOfTransaction,
> merchantAlternateLanguagePreference, merchantNameAlternateLanguage, merchantCityAlternateLanguage`.
> Payment flow wired in `vue_frontend/src/services/invoices.js`: `generate-qr` → `qr-image` →
> poll `check-transaction` → `POST /api/invoices/{id}/confirm-payment {md5}`.

#### Test / role probes — `/api/test`

| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/test/customer` | CUSTOMER |
| GET | `/api/test/admin` | ADMIN |
| GET | `/api/test/manager` | MANAGER |
| GET | `/api/test/staff` | STAFF |
| GET | `/api/test/any` | JWT |

### 2.7 Known backend issues / gaps (fix candidates)

1. **Mutex/duplicate user-management** endpoints (`/api/auth/users` vs `/api/users`). Consolidate.
2. **`GET /api/vehicle-images/**` is not public** although the vehicle detail page needs it
   logged-out. Add it to the `SecurityConfig` public allow-list.
3. **`/api/services` write endpoints are unguarded** (any authenticated user). Add
   `@PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")`.
4. ✅ **Discounts & services GETs for customers — RESOLVED.** `GET /api/services` is now in the
   `SecurityConfig` public allow-list (any visitor) and `GET /api/discounts/active` is open to any
   signed-in user and filters expired/exhausted codes server-side. Frontend uses both
   (`reservations.js`).
5. ✅ **Reservation overlap check — RESOLVED.** `ReservationServiceImpl` now rejects overlapping
   PENDING/CONFIRMED reservations for the same vehicle on create/update. Same guard added for
   rentals (item 11).
6. **AuditLog** is written nowhere yet (endpoints + service exist).
7. **No real analytics/reporting endpoint** — `Dashboard.vue` computes stats client-side.
8. **No server-side vehicle search/filter** — everything client-side.
9. ✅ **Bakong contracts pinned — RESOLVED.** Shapes live in §2.6 Bakong; public flow in
   `vue_frontend/src/services/invoices.js` is fixed (`generate-qr`, `qr-image`, `check-transaction`,
   `confirm-payment`). ⚠ Only the admin-only `invoice.service.js` still has stale calls — not in the
   public path.
10. **Test coverage** — `src/test/java` essentially empty; add `@WebMvcTest`, `@DataJpaTest`,
    service unit tests (price math, discount, reservation status transitions, Bakong).
11. **IllegalState/no overlap guard on rentals** — creating a rental for an already-rented vehicle.

---

## 3. Frontend — Vue 3 SPA (current state)

### 3.1 Tech stack

- **Framework:** Vue 3 (`<script setup>` + Composition API)
- **Build:** Vite (`@vitejs/plugin-vue`)
- **Styling:** Tailwind CSS 4 via `@tailwindcss/vite`
- **Routing:** Vue Router (history mode); `@` alias → `src/`
- **HTTP:** Axios (`services/api.js`, `baseURL: "/api"`, Vite proxy → `http://localhost:8080`)
- **I18n:** `vue-i18n` (legacy:false), locales `en` / `km` (`src/i18n/`)
- **State:** lightweight `reactive()` stores (`auth`, `siteSettings`, `theme`) — **not Pinia**
- **Charts:** `chart.js` + `vue-chartjs` (dashboard usage/analytics)
- **Package manager:** npm

### 3.2 Frontend commands

```bash
cd vue_frontend
npm install       # install deps
npm run dev       # dev server (Vite)
npm run build     # production build
npm run preview   # preview the build
```

### 3.3 Frontend folder structure

```
vue_frontend/src/
├── App.vue                       # router-view + page transition (header NOT global anymore)
├── main.js                       # router + i18n + fetchSettings() before mount
├── style.css                     # Tailwind + CSS-variable theming (:root + .dark) + fonts
├── components/
│   ├── base/                     # BaseButton.vue, BaseInput.vue
│   ├── common/                   # LanguageSwitcher.vue, ThemeToggle.vue
│   ├── layout/                   # AppHeader.vue (legacy hex), SiteHeader.vue (themed),
│   │                              #   AppSidebar.vue, NotificationBell.vue
│   ├── notifications/            # NotificationBell.vue (dup of layout/ — see cleanup)
│   ├── reviews/                  # StarRating.vue, ReviewList.vue, ReviewForm.vue
│   ├── ui/                       # DataTable.vue, Modal.vue
│   └── vehicles/                 # VehicleCard.vue
├── composables/                  # useFetch.js, useSidebar.js, useTheme.js
├── i18n/                         # index.js + locales/{en,km}.json
├── layouts/                      # AuthLayout.vue (login/register), BackLayout.vue (admin shell)
├── pages/
│   ├── auth/                     # LoginForm, RegisterForm, ForgotPassword, ResetPassword, OAuth2Redirect
│   ├── dashboard/                # Dashboard + AdminProfile + all 14 admin CRUD pages + Settings
│   ├── explore/                  # Explore.vue
│   ├── favorites/                # Favorites.vue
│   ├── home/                     # Home.vue (+ home/home/Card.vue — odd nesting)
│   ├── invoices/                 # InvoiceList.vue, InvoiceDetail.vue
│   ├── notifications/            # Notifications.vue
│   ├── preview/                  # Preview.vue (demo landing) + components/ {LeftPannel, RightPannel, PreText}
│   ├── profile/                  # Profile.vue
│   ├── rentals/                  # RentalHistory.vue
│   ├── reservations/             # ReservationForm.vue, MyReservations.vue
│   ├── vehicles/                 # VehicleDetail.vue
│   └── NotFound.vue
├── router/index.js               # routes + auth/role guards + /oauth2/redirect
├── services/                     # api.js + feature services (see below)
└── stores/                       # auth.store.js, siteSettings.store.js, theme.store.js
```

### 3.4 Frontend services (`src/services/`)

| File | Endpoints used | Notes |
|------|----------------|-------|
| `api.js` | axios base, `TOKEN_KEY` | JWT interceptor; 401 only logs out when a token was attached; 403 is left to callers |
| `vehicles.js` | vehicles, vehicle-images, reviews/vehicle, favorites | Normalizers `normalizeVehicle*`, `normalizeImage`, `normalizeReview` |
| `reservations.js` | reservations, locations, services, discounts | `calculatePriceBreakdown` helper; **`getDiscounts`/`getServices` will 403 for customers** |
| `rentals.js` | rentals, rental-documents, inspections | **calls removed `/rental-documents/{id}/upload`**; `RENTAL_STATUS_STEPS` timeline |
| `invoices.js` | invoices, v1/bakong | ⚠ **stale Bakong endpoints** (`check-payment`) — duplicate of `invoice.service.js` |
| `invoice.service.js` | invoices, v1/bakong | ⚠ same stale Bakong shapes; used by `InvoiceManagement.vue` |
| `favorites.js` | favorites | tiny — add/remove live in `vehicles.js` |
| `reviews.js` | reviews | duplicate of `reviews.service.js` |
| `reviews.service.js` | reviews (+ `/visibility`) | used by `ReviewManagement.vue` |
| `notifications.js` | notifications/me/inbox | duplicate of `notifications.service.js` |
| `notifications.service.js` | inbox, unread-count, read, read-all, delete, notify, all | used by admin + bell |
| `profile.js` | user-profiles/me (+login-history) | duplicate of `profile.service.js` |
| `profile.service.js` | me, updateMe, change-password, login-history | used by `AdminProfile.vue` + auth store |
| `dashboard.js` | vehicles, reservations, rentals | **client-side stats** — swap for `/api/admin/stats` when built |
| `discount.service.js` | discounts | calls nonexistent `GET /api/discounts/active` |
| `maintenance.service.js` | maintenance-records | CRUD |
| `siteSettings.service.js` | settings | `getSiteSettings` / `updateSiteSettings` |

> **Cleanup:** `invoices.js` ↔ `invoice.service.js`, `reviews.js` ↔ `reviews.service.js`,
> `notifications.js` ↔ `notifications.service.js`, `profile.js` ↔ `profile.service.js` are
> duplicates. Keep one per feature — prefer the `.service.js` variant used by the admin pages OR
> merge into the plain files used by customer pages, but not both.

### 3.5 Frontend routes (router/index.js, verified)

| Path | Component | Guard |
|------|-----------|-------|
| `/` | → `/preview` | — |
| `/preview` | Preview (demo landing) | — |
| `/home` | Home (real data) | — |
| `/explore` | Explore | — (`?q=` seeds search) |
| `/vehicles/:id` | VehicleDetail | — |
| `/login` | AuthLayout (mode=login) | guestOnly |
| `/register` | AuthLayout (mode=register) | guestOnly |
| `/forgot-password` | ForgotPassword | guestOnly |
| `/reset-password` | ResetPassword | — |
| `/oauth2/redirect` | OAuth2Redirect | — |
| `/reservations` | ReservationForm | requiresAuth (`?vehicleId=`) |
| `/my-reservations` | MyReservations | requiresAuth |
| `/favorites` | Favorites | requiresAuth |
| `/my-rentals` | RentalHistory | requiresAuth |
| `/my-invoices` | InvoiceList | requiresAuth |
| `/my-invoices/:id` | InvoiceDetail | requiresAuth |
| `/notifications` | Notifications | requiresAuth |
| `/profile` | Profile | requiresAuth |
| `/dashboard` (BackLayout) | children | requiresAuth + roles [ADMIN, MANAGER, STAFF] |
| `/dashboard` | Dashboard (stats) | roles |
| `/dashboard/profile` | AdminProfile | roles |
| `/dashboard/vehicles` | VehicleManagement | roles |
| `/dashboard/locations` | LocationManagement | roles |
| `/dashboard/reservations` | ReservationManagement | roles |
| `/dashboard/rentals` | RentalManagement | roles |
| `/dashboard/customers` | CustomerManagement | **ADMIN** |
| `/dashboard/discounts` | DiscountManagement | **ADMIN** |
| `/dashboard/invoices` | InvoiceManagement | roles |
| `/dashboard/reviews` | ReviewManagement | roles |
| `/dashboard/notifications` | NotificationManagement | roles |
| `/dashboard/maintenance` | MaintenanceManagement | roles |
| `/dashboard/services` | ServiceManagement | roles |
| `/dashboard/audit-logs` | AuditLogManagement | **ADMIN, MANAGER** |
| `/dashboard/login-history` | LoginHistoryManagement | **ADMIN, MANAGER** |
| `/dashboard/settings` | Settings | **ADMIN, MANAGER** |
| `/:pathMatch(.*)*` | NotFound | — |

> **All 14 admin child routes are now registered and implemented.** The old "11 routes 404"
> gap is closed. Heading is auto-derived from the last URL segment in `BackLayout`.

### 3.6 Frontend design system — TWO systems in the codebase

The app is mid-migration from hard-coded hex to CSS-variable theming. Match whichever system
the file you're touching already uses.

**A. New "themed" system (CSS variables + dark mode + i18n)** — `SiteHeader.vue`, `Home.vue`,
`BackLayout.vue`, `AppSidebar.vue`, most dashboard pages, `NotificationBell`:

- Tokens defined in `style.css` (`:root` light / `.dark` overrides):
  `--color-bg, --color-surface, --color-border, --color-text, --color-text-secondary,
  --color-primary, --color-primary-hover, --color-primary-light`.
- Applied via inline `:style="{ color: 'var(--color-text)' }"` etc. — **not** Tailwind color
  classes (so Tailwind's dark: variant is not what toggles).
- Dark mode: `theme.store.js` toggles the `.dark` class on `<html>` (`useTheme()` composable);
  defaults to OS `prefers-color-scheme`.
- I18n: `useI18n()` + `$t('...')` keys under `en`/`km`; sidebar + nav + theme labels are
  translated. `setLocale()` updates `<html lang>`.
- Fonts: Inter + Noto Sans Khmer (+ Kantumruy Pro via `:lang(km)`).

**B. Legacy "hard-hex" system** — `AppHeader.vue`, `AuthLayout.vue`, customer pages like
`VehicleDetail`, `VehicleCard`:

- Palette: primary `#3D5FE0`, hover `#3350C0`, light `#E9EDFB`, dark `#1A2036`, input `#F3F4F6`,
  hover `#F9FAFB`, border `#E5E7EB`, muted `#9CA3AF`, secondary `#6B7280`, danger `#DC2626`,
  favorite `#EF4444`, success `#22C55E`.
- Pill inputs, `rounded-full` buttons, `rounded-2xl` cards, `#1A2036` headings.

**Style recipes that apply to both** (keep using these):

```html
<div class="flex items-center gap-3 rounded-full bg-[#F3F4F6] px-5 py-3.5">…pill input…</div>
<button class="w-full rounded-full bg-[#3D5FE0] py-3.5 text-sm font-semibold text-white hover:bg-[#3350C0]">…</button>
<article class="overflow-hidden rounded-2xl border border-[#E5E7EB]">…</article>
<div class="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-600">…error…</div>
<span class="rounded-full px-2.5 py-0.5 text-xs font-semibold bg-[#E9EDFB] text-[#3D5FE0]">…status…</span>
<div class="h-28 animate-pulse rounded-2xl bg-[#F3F4F6]"></div>
```

### 3.7 Frontend conventions

- **Pages** live in `pages/<feature>/<Name>.vue` mirroring the route.
- **HTTP** goes through `services/api.js`; per-feature calls live in a `services/<feature>.js`
  file. Reuse existing functions — don't duplicate. **Normalizers exist because backend DTO field
  names were guessed** — verify against §2.6 / Swagger, then fix the normalizer, not the template.
- **Admin CRUD pages** follow the `VehicleManagement.vue` pattern (DataTable + Modal + api calls
  + error banner + saving state). New admin pages are children of `/dashboard` (BackLayout).
- **Auth** via `useAuthStore()`: `login`, `logout`, `isAuthenticated()`, `hasRole(...)`,
  `defaultRedirect()`. Route guards are `meta.requiresAuth` / `meta.roles` / `meta.guestOnly` —
  don't hand-roll guards in pages.
- **Shared components** in `components/` (check before writing a new one): `DataTable`, `Modal`,
  `VehicleCard`, reviews components, `ThemeToggle`, `LanguageSwitcher`.
- **Site defaults** come from `siteSettings.store.js` (fetched in `main.js` before mount): site
  name, logo, favicon, contact info — use it instead of hardcoding the brand.
- **README/AGENTS:** `vue_frontend/AGENTS.md` is the frontend pointer; this file is the source
  of truth.

### 3.8 Frontend status & known mismatches

| Area | Status |
|------|--------|
| Auth (login/register/forgot/reset/OAuth2) | ✅ wired |
| Public browse (Home/Explore/VehicleDetail) | ✅ real data; brand filter from `vehicle.brand` (naïve — see below) |
| Reservation flow | ✅ but promo/service lookups 403 for customers |
| My reservations/rentals/invoices/favorites/notifications/profile | ✅ |
| Admin dashboard + all 16 dashboard pages | ✅ implemented |
| i18n (en/km) + dark mode | ⚠ partially — admin header/sidebar themed, but several customer pages still hard-hex |
| Bakong payment UI | ⚠ stale endpoints, QR/polling unfinished |
| Rental doc upload | ⚠ calls removed endpoint |
| Vehicle images on cards/detail | 🟡 `VehicleCard` still SVG placeholder; detail uses attachment flow |
| Google/frontend OAuth | ✅ `/oauth2/redirect` decodes `?token=` |

**Known frontend issues to fix:**
- `Home.vue` brand filter uses `v.brand` but the real field is `brandName` → brand chips show
  "Vehicle" / wrong values. Fix in `normalizeVehicle`.
- `SiteHeader.vue` brand name is hard-coded `"Wheelo"` (should come from `siteSettings.store.js`).
- Duplicate header components (`layout/AppHeader.vue` legacy vs `layout/SiteHeader.vue` themed),
  and duplicate `NotificationBell` under `components/notifications/`.
- `services/rentals.js` upload, `services/{invoices,invoice}.js` Bakong, `discount.service.js`
  `/active` — all call stale/nonexistent endpoints.
- Duplicate service files (see §3.4 cleanup note).
- `/` still redirects to `/preview` (demo landing) — probably revisit for production.

---

## 4. 🎯 Public Website / Marketing Site — redesign roadmap

The user's product direction: **turn the customer-facing site into a real marketing/booking
website** with more features, while the dashboard/admin area stays as-is. Everything below is a
**priority-ordered plan** — start from the "must do" list.

### 4.1 What the public site looks like today

- `/home` (Home.vue, themed, real vehicle data) is the de-facto landing — but `/` still serves
  the static `/preview` demo, not `/home`.
- `SiteHeader` (brand "Wheelo" hardcoded) + hero carousel + brand chips + "Popular cars" grid.
- `/explore` (client-side search + type filter), `/vehicles/:id` detail with reviews + favorites.
- `AuthLayout` login/register already looks polished (brand panel, stats).

### 4.2 Phase A — Fix the foundation first (prereq for any redesign)

- [ ] Switch `/` → `/home` (remove the `/preview` demo redirect once the landing is real).
- [ ] Resolve the two-header problem: keep **one** public header. Recommend `SiteHeader.vue`
      (themed, i18n) fed from `siteSettings.store.js` (visitName + logoUrl), delete/absorb
      `AppHeader.vue` legacy hex or migrate it to CSS vars. Keep 1 `NotificationBell`.
- [ ] Fix `vehicles.js` normalizers + `Home.vue` brand chips (`brandName`, not `brand`), and
      terrain: wire real images into `VehicleCard` (attachment flow) so the fleet grid shows cars.
- [ ] Make the backend public-read endpoints non-403: `GET /api/vehicle-images/**`,
      `GET /api/services`, `GET /api/discounts?active=true` for customers (§2.7 items 2–4).
- [ ] Fix Bakong endpoints + rental-doc upload on the customer side (§3.8).
- [ ] Migrate customer pages (VehicleDetail, VehicleCard, Explore, Profile, invoices/reservations/
      rentals lists) to the CSS-variable/themed system so dark mode + Khmer i18n work everywhere.

### 4.3 Phase B — Marketing landing page (`/home` → real landing)

- [ ] Hero: keep the carousel; add site settings-driven headline/tagline, CTA buttons
      ("Browse cars", "Rent today"), and a background image/pattern (use Cloudinary asset).
- [ ] Sections (each a leaf component under `components/home/`):
      1. **Hero + search bar** (vehicle type, pickup date, return date → `/explore` with params).
      2. **Trust bar** — stats from real counts (vehicles, locations, active rentals) or
         siteSettings.
      3. **Popular fleet** — top-rated/featured vehicles (reuse `VehicleCard`, real images).
      4. **How it works** — 3–4 steps (Search → Reserve → Pick up → Drive).
      5. **Locations** — branch cards from `GET /api/locations`.
      6. **Reviews/testimonials** — recent 5★ reviews (`GET /api/reviews`) or a new
         `GET /api/reviews/featured`.
      7. **Footer** — contact info from `siteSettings` + facebook/telegram links + quick links.
- [ ] Site header: add nav for Home/Explore/Fleet/How it works/Locations + Contact; login/signup
      buttons; use `siteSettings.siteName`/`logoUrl`.
- [ ] i18n: all landing strings in `en.json` / `km.json`.

### 4.4 Phase C — Fleet & detail upgrades

- [ ] **Server-side search/filter**: add `?q=&type=&brandId=&minPrice=&maxPrice=&seats=&
      transmission=&fuelType=&availableFrom=&availableTo=` to `GET /api/vehicles`, then switch
      Explore to it (with pagination) instead of client-side filtering.
- [ ] **Filter sidebar** on `/explore` (type, brand, seats, transmission, fuel, price range),
      sort (price asc/desc, newest, rating), pagination.
- [ ] **Availability widget** on `/vehicles/:id` — date range + pickup/return location →
      disable past/unavailable dates server-side (`GET /api/vehicles/{id}/availability`).
- [ ] **Vehicle detail**: image gallery (thumbnails + lightbox), spec grid (seats/doors/luggage/
      fuel/transmission/yr/mileage), price card, insurance/add-on services selector, discount
      code box (only if backend exposes a safe public lookup), favorite, share, reviews.
- [ ] **Realtime feel**: skeleton loaders, empty states, optimistic favorite toggles, toasts for
      mutations (add a small toast composable `composables/useToast.js`).

### 4.5 Phase D — Booking & account flows (customer)

- [x] **Booking page** (`/booking/:vehicleId`, legacy `/reservations?vehicleId=` still works):
      dates & locations, add-ons, discount code, client-side preview via `calculatePriceBreakdown`.
      Submit → `POST /api/reservations` (auto-creates Rental+Invoice for CUSTOMER) → redirect to
      `/payment/{invoiceId}` with the invoice id from the response.
- [x] **Payment UI**: `Payment.vue` at `/payment/:invoiceId` — generate QR (`/generate-qr`),
      render PNG (`/qr-image`), poll `/check-transaction {md5}` (~4s), then
      `POST /api/invoices/{id}/confirm-payment {md5}`. Idempotent; shows PAID state.
- [ ] **Price endpoint**: backend `POST /api/reservations/price` (authoritative) to replace the
      client-side preview math in the booking form.
- [ ] My Rentals / My Invoices: status timelines, countdown badges, doc upload
      (via the working attachment flow), cancel/confirm dialogs.
- [ ] **Notifications center**: mark read / read all, types, unread badge (endpoints exist).
- [ ] **Profile**: avatar upload (attachment flow), change password (exists), language + theme
      preferences, favorite list.

### 4.6 Phase E — Platform polish for a public site

- [ ] **Public site settings** fully used: name, logo, favicon, contact — no hardcoded brand.
- [ ] **SEO/OG tags** per route (plugin `@unhead/vue` or manual `useHead`): title from
      siteSettings, description, image; proper `lang`/`dir`.
- [ ] **Loading/error boundary** for public pages; 404 page restyled to the marketing look.
- [ ] **Backend analytics for the marketing site** (optional): popular vehicles, featured
      reviews, fleet counts — endpoints to be added.

### 4.7 Ordering rule for agents

When asked to "add a feature to the website," do this first: (1) check whether it's blocked by a
§2.7 / §3.8 known-fix (backend 403 or stale endpoint) — fix the API contract first; (2) check
which design system the page uses and stay in it (or migrate it); (3) put new UI in
`components/<domain>/` and new logic in `services/` / `stores/` / `composables/`; (4) feel free
to add backend endpoints + DTOs following §2.5 conventions to remove client-side hacks.

---

## 5. Page status overview (frontend)

| Page | Path | Status |
|------|------|--------|
| Preview (demo) | `/preview` | 🟡 demo — reconsider as root |
| Home / landing | `/home` | ✅ real data; needs marketing sections (Phase B) |
| Explore | `/explore` | ✅ client-side filter; add server-side + pagination |
| Vehicle detail | `/vehicles/:id` | ✅; add gallery/availability (Phase C) |
| Reservation form | `/reservations` | ✅; disc./service lookups 403 for customers |
| My reservations | `/my-reservations` | ✅ |
| Favorites | `/favorites` | ✅ |
| Rental history | `/my-rentals` | ✅; doc upload stale |
| Invoices | `/my-invoices`(+`/:id`) | ✅; Bakong wiring open |
| Notifications | `/notifications` | ✅ |
| Profile | `/profile` | ✅ |
| Auth pages | `/login` `/register` `/forgot-password` `/reset-password` `/oauth2/redirect` | ✅ |
| Dashboard home | `/dashboard` | ✅ (client-side stats) |
| All 14 admin CRUD + profile + settings | `/dashboard/*` | ✅ implemented |

---

## 6. "To do next" — consolidated backlog

### Backend (API completeness & correctness)
- [ ] Add `GET /api/vehicle-images/**` to the public allow-list (or confirm intended JWT-only).
- [ ] Gate `/api/services` writes behind staff roles; decide public read for the catalog.
- [ ] Add a public, safe discount lookup (`GET /api/discounts?active=true`) for the booking form.
- [ ] Reservation/Rental overlap guard (double-booking prevention).
- [ ] Server-side vehicle search/filter + pagination.
- [ ] Consolidate `/api/users` vs `/api/auth/users`.
- [ ] `POST /api/reservations/price` (authoritative price breakdown).
- [ ] Analytics endpoints: revenue, rentals/mo, utilization, popular vehicles (replace
      `dashboard.js` client-side stats).
- [ ] Wire `auditLogService.log(...)` into mutating services.
- [ ] Pin Bakong request/response DTOs; add tests.
- [ ] Tests: `@WebMvcTest` / `@DataJpaTest` / Mockito for price, discount, statuses, Bakong, auth.
- [ ] Seeding/demo-data loader for dev.
- [ ] Unify error JSON shape (`{ status, message, ... }`) with 403/404/500 codes.

### Frontend — customer site redesign (see §4 for full plan)
- [ ] Phase A foundation fixes (§4.2) — do these before anything else.
- [ ] Phase B landing page sections.
- [ ] Phase C explore/filters/availability/gallery.
- [ ] Phase D booking wizard + payment UI + doc upload rework.
- [ ] Phase E SEO, toasts, empty/loading states, brand-driven settings everywhere.

### Frontend — cleanup
- [ ] Remove duplicate service files (`invoices`, `reviews`, `notifications`, `profile` vs
      their `.service.js` twins).
- [ ] Resolve duplicate headers/bells; delete `components/notifications/NotificationBell.vue`.
- [ ] `home/home/Card.vue` weird nesting — move/flatten.
- [ ] Make root redirect `/` → `/home` in production.
- [ ] Finish dark-mode/i18n migration of legacy-hex customer pages.

### Cross-cutting
- [ ] Document running both servers locally; `.env.example` complete.
- [ ] Production: Spring profile env, Vercel SPA rewrite (`vercel.json`), OAuth success-handler
      redirect target, CORS tightened in `CorsConfig` (currently `*`).

---

## Notes for agents

- Don't add a new architectural layer or dependency unless the task actually needs it.
- Keep controller methods thin; business logic lives in services.
- New resource → create matching files across `model`, `enums` (if needed), `repository`,
  `dto/request/<resource>`, `dto/response/<resource>`, `service` (+`impl`), `controller`.
  Follow the naming: `<Resource>Controller`, `<Resource>Service`, `<Resource>ServiceImpl`,
  `<Resource>Repository`, `<Resource>RequestDTO`, `<Resource>ResponseDTO`.
- Frontend: network calls in `services/<feature>.js`, shared UI in `components/<domain>` +
  `components/ui` + `components/layout`, pages in `pages/<feature>/`. Store data in
  `stores/`/`composables/`. Match the design system of the file you edit (§3.6).
- Verify with `./mvnw test` (backend) and `npm run build` (frontend) before calling a change done.
- After any API change, update §2.6 and the "known gaps" list — agents and the UI team rely on it.