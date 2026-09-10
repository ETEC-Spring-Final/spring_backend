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
│                       #   JacksonConfig, UploadConfig, AuthenticationEventListener,
│                       #   CustomOAuth2UserService, CustomOAuth2User, OAuth2AuthenticationSuccessHandler
├── controller/         # @RestController — HTTP layer only, delegates to service
├── dto/
│   ├── request/<resource>/    # inbound validated payloads
│   └── response/<resource>/   # outbound payloads
├── enums/              # CarTypeEnum, FuelTypeEnum, RoleEnum, StatusEnum, RentalStatusEnum,
│                       #   ReservationStatusEnum, InspectionTypeEnum, DiscountTypeEnum,
│                       #   AuditActionEnum, FuelLevelEnum, DocumentTypeEnum, GenderEnum,
│                       #   InvoiceStatusEnum, MaintenanceStatusEnum, MaintenanceTypeEnum,
│                       #   NotificationTypeEnum, AuthProviderEnum, CarTypeEnum, TransmissionEnum
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

### Backend API reference (complete)

#### Auth (public)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, returns JWT |
| POST | `/api/auth/logout` | JWT | Logout |
| POST | `/api/auth/forgot-password` | Public | Request password reset email |
| POST | `/api/auth/reset-password` | Public | Reset password with token |

#### Users

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/users` | ADMIN | List all users |
| GET | `/api/users/{id}` | ADMIN | Get user by ID |
| PUT | `/api/users/{id}` | ADMIN | Update user |
| DELETE | `/api/users/{id}` | ADMIN | Delete user |
| GET | `/api/user-profiles/me` | JWT | Get my profile |
| PUT | `/api/user-profiles/me` | JWT | Update my profile |
| GET | `/api/user-profiles/me/login-history` | JWT | My login history |

#### Vehicles

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/vehicles` | Public | List all vehicles |
| GET | `/api/vehicles/{id}` | Public | Get vehicle by ID |
| POST | `/api/vehicles` | ADMIN | Create vehicle |
| PUT | `/api/vehicles/{id}` | ADMIN | Update vehicle |
| DELETE | `/api/vehicles/{id}` | ADMIN | Delete vehicle |

#### Vehicle Images

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/vehicle-images/{vehicleId}` | Public | Get images for a vehicle |
| POST | `/api/vehicle-images` | ADMIN | Upload vehicle image |
| DELETE | `/api/vehicle-images/{id}` | ADMIN | Delete vehicle image |

#### Locations

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/locations` | Public | List all locations |
| GET | `/api/locations/{id}` | Public | Get location by ID |
| POST | `/api/locations` | ADMIN/MANAGER/STAFF | Create location |
| PUT | `/api/locations/{id}` | ADMIN/MANAGER/STAFF | Update location |
| DELETE | `/api/locations/{id}` | ADMIN/MANAGER/STAFF | Delete location |

#### Reservations

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/reservations` | ADMIN/MANAGER/STAFF | List all reservations |
| GET | `/api/reservations/{id}` | JWT | Get reservation by ID |
| GET | `/api/reservations/my-reservations` | JWT | My reservations |
| POST | `/api/reservations` | JWT | Create reservation |
| PUT | `/api/reservations/{id}` | ADMIN/MANAGER/STAFF | Update reservation |
| PATCH | `/api/reservations/{id}/status?status=X` | ADMIN/MANAGER/STAFF | Change reservation status |
| PATCH | `/api/reservations/{id}/cancel` | JWT | Cancel reservation |
| DELETE | `/api/reservations/{id}` | ADMIN/MANAGER/STAFF | Delete reservation |

#### Rentals

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/rentals` | ADMIN/MANAGER/STAFF | List all rentals |
| GET | `/api/rentals/{id}` | JWT | Get rental by ID |
| GET | `/api/rentals/my-rentals` | JWT | My rentals |
| POST | `/api/rentals` | ADMIN/MANAGER/STAFF | Create rental |
| PUT | `/api/rentals/{id}` | ADMIN/MANAGER/STAFF | Update rental |
| PATCH | `/api/rentals/{id}/status?status=X` | ADMIN/MANAGER/STAFF | Change rental status |
| DELETE | `/api/rentals/{id}` | ADMIN/MANAGER/STAFF | Delete rental |

#### Rental Documents

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/rental-documents/my-rental-document` | JWT | My uploaded documents |
| POST | `/api/rental-documents/{rentalId}/upload` | JWT | Upload driver document (multipart) |

#### Favorites

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/favorites` | JWT | List my favorites |
| POST | `/api/favorites/{vehicleId}` | JWT | Add to favorites |
| DELETE | `/api/favorites/{vehicleId}` | JWT | Remove from favorites |

#### Reviews

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/reviews/vehicle/{vehicleId}?page=X&size=Y` | Public | Reviews for a vehicle (paginated) |
| GET | `/api/reviews/vehicle/{vehicleId}/count?rating=X` | Public | Count reviews by rating |
| GET | `/api/reviews/my-reviews` | JWT | My reviews (paginated) |
| GET | `/api/reviews` | ADMIN/MANAGER/STAFF | All reviews (paginated) |
| GET | `/api/reviews/{id}` | JWT | Get review by ID |
| POST | `/api/reviews` | JWT | Create review |
| PUT | `/api/reviews/{id}` | JWT | Update review |
| DELETE | `/api/reviews/{id}` | JWT | Delete review |

#### Invoices

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/invoices/my-invoices` | JWT | My invoices |
| GET | `/api/invoices` | ADMIN/MANAGER/STAFF | All invoices |
| GET | `/api/invoices/{id}` | JWT | Get invoice by ID |
| POST | `/api/invoices` | ADMIN/MANAGER/STAFF | Create invoice |
| PUT | `/api/invoices/{id}` | ADMIN/MANAGER/STAFF | Update invoice |
| DELETE | `/api/invoices/{id}` | ADMIN/MANAGER/STAFF | Delete invoice |

#### Discounts

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/discounts` | Public | List available discounts |
| POST | `/api/discounts` | ADMIN | Create discount |
| PUT | `/api/discounts/{id}` | ADMIN | Update discount |
| DELETE | `/api/discounts/{id}` | ADMIN | Delete discount |

#### Discount Usage

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/discount-usages` | ADMIN | List all discount usages |
| POST | `/api/discount-usages` | ADMIN | Record discount usage |

#### Notifications

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/notifications/me/inbox` | JWT | My notification inbox |
| POST | `/api/notifications/{userId}/notify` | ADMIN/MANAGER/STAFF | Send notification to user |

#### Inspections

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/inspections/rental/{rentalId}` | JWT | Inspection report for a rental |
| POST | `/api/inspections` | ADMIN/MANAGER/STAFF | Create inspection |
| PUT | `/api/inspections/{id}` | ADMIN/MANAGER/STAFF | Update inspection |

#### Maintenance Records

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/maintenance-records` | ADMIN/MANAGER/STAFF | List all maintenance records |
| GET | `/api/maintenance-records/{id}` | ADMIN/MANAGER/STAFF | Get by ID |
| POST | `/api/maintenance-records` | ADMIN/MANAGER/STAFF | Create maintenance record |
| PUT | `/api/maintenance-records/{id}` | ADMIN/MANAGER/STAFF | Update maintenance record |
| DELETE | `/api/maintenance-records/{id}` | ADMIN/MANAGER/STAFF | Delete maintenance record |

#### Services (maintenance services / add-ons)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/services` | Public | List all services |
| GET | `/api/services/{id}` | Public | Get service by ID |
| POST | `/api/services` | ADMIN/MANAGER/STAFF | Create service |
| PUT | `/api/services/{id}` | ADMIN/MANAGER/STAFF | Update service |
| DELETE | `/api/services/{id}` | ADMIN/MANAGER/STAFF | Delete service |

#### Attachments

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/attachments` | ADMIN | List all attachments |
| POST | `/api/attachments` | ADMIN | Upload attachment |
| DELETE | `/api/attachments/{id}` | ADMIN | Delete attachment |

#### Admin-only

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/admin/audit-logs` | ADMIN/MANAGER | List audit logs |
| GET | `/api/admin/login-history` | ADMIN/MANAGER | All users' login history |

#### Bakong Payments

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/bakong/generate-qr` | Public | Generate Bakong QR code |
| POST | `/api/v1/bakong/check-payment` | Public | Check payment status |

---

## 3. Frontend — Vue 3 SPA (current state)

### Tech stack

- **Framework:** Vue 3 (`<script setup>` style) + Composition API
- **Build:** Vite 8 (`@vitejs/plugin-vue`)
- **Styling:** Tailwind CSS 4 via `@tailwindcss/vite` (fonts via Google Fonts in `style.css`)
- **Routing:** Vue Router (history mode) — `@` alias → `src/`
- **HTTP:** Axios (central `services/api.js`, `baseURL: "/api"`, Vite proxy → `http://localhost:8080`)
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

### Frontend folder structure (current)

```
vue_frontend/src/
├── App.vue                       # root — mounts <AppHeader /> globally + <RouterView />
├── main.js                       # createApp + router
├── style.css                     # global styles + Tailwind + Google Fonts
├── components/
│   ├── base/                     # BaseButton.vue, BaseInput.vue (reusable primitives)
│   ├── layout/                   # AppHeader.vue, AppSidebar.vue, NotificationBell.vue
│   ├── ui/                       # DataTable.vue, Modal.vue
│   ├── vehicles/                 # VehicleCard.vue
│   └── reviews/                  # StarRating.vue, ReviewList.vue, ReviewForm.vue
├── composables/                  # useFetch.js
├── layouts/                      # FrontLayout.vue (placeholder/unused), BackLayout.vue (used)
│                                  #   AppHeader.vue (duplicate — see cleanup note)
├── pages/
│   ├── auth/                     # Login, Register, ForgotPassword, ResetPassword, OAuth2Redirect
│   ├── dashboard/                # Dashboard.vue, VehicleManagement.vue, LocationManagement.vue
│   ├── explore/                  # Explore.vue
│   ├── vehicles/                 # VehicleDetail.vue
│   ├── reservations/             # ReservationForm.vue, MyReservations.vue
│   ├── favorites/                # Favorites.vue
│   ├── rentals/                  # RentalHistory.vue
│   ├── invoices/                 # InvoiceList.vue, InvoiceDetail.vue
│   ├── notifications/            # Notifications.vue
│   ├── profile/                  # Profile.vue (info + login history + my reviews + favorites)
│   ├── home/                     # Home.vue (real data), home/Card.vue
│   ├── preview/                  # demo landing (unused for real app; Preview.vue)
│   └── NotFound.vue
├── router/                       # index.js (routes + auth/role guards)
├── services/                     # api.js + per-domain services (see below)
└── stores/                       # auth.store.js (reactive store, localStorage)
```

### Frontend services (`src/services/`)

| File | Exports | Endpoints |
|------|---------|-----------|
| `api.js` | `api` (axios), `TOKEN_KEY` | baseURL `/api`, JWT interceptor, 401 → login |
| `vehicles.js` | fetchVehicles, fetchVehicleById, fetchVehicleImages, fetchVehicleReviews, fetchMyFavorites, addFavorite, removeFavorite, normalizeVehicle, normalizeVehicleDetail, normalizeImage, normalizeReview | vehicles, vehicle-images, reviews/vehicle, favorites |
| `reservations.js` | createReservation, getMyReservations, getReservationById, cancelReservation, updateReservationStatus, getLocations, getServices, getDiscounts, **calculatePriceBreakdown** | reservations + locations/services/discounts lookups |
| `rentals.js` | getMyRentals, getRentalById, uploadRentalDocument, getMyRentalDocuments, getRentalInspection, **RENTAL_STATUS_STEPS**, **rentalStatusStepIndex** | rentals, rental-documents, inspections |
| `invoices.js` | default: myInvoices, getById, generateQr, checkPayment | invoices, v1/bakong |
| `favorites.js` | getFavorites | favorites |
| `reviews.js` | default: forVehicle, countByRating, myReviews, getById, create, update, remove | reviews |
| `notifications.js` | default: inbox | notifications/me/inbox |
| `profile.js` | default: me, updateMe, loginHistory | user-profiles/me |
| `dashboard.js` | fetchDashboardStats (client-side stats from /vehicles + /reservations + /rentals) | — |

> **Note:** `invoices.js` generateQr/checkPayment request/response shapes are marked
> "confirm against backend" in comments — verify against `BakongController` when wiring payment.
> `dashboard.js` computes stats client-side; swap for a real `/api/admin/stats` endpoint when the
> backend adds one.

### Frontend routes (router/index.js)

Nested routes under `/dashboard` are wrapped by `BackLayout` (sidebar + header). All other routes
render directly; the global `AppHeader` shows on every page via `App.vue`.

| Path | Component | Auth | Notes |
|------|-----------|------|-------|
| `/` | — | — | redirects to `/preview` (demo) |
| `/preview` | Preview | — | landing/demo |
| `/home` | Home | — | real landing, fetches vehicles |
| `/explore` | Explore | — | public browse/search/filter, `?q=` from Home |
| `/vehicles/:id` | VehicleDetail | — | public; Rent/favorite actions check auth |
| `/login` | Login | guestOnly | wired to backend |
| `/register` | Register | guestOnly | wired to backend |
| `/forgot-password` | ForgotPassword | guestOnly | |
| `/reset-password` | ResetPassword | — | accessible from reset email |
| `/oauth2/redirect` | OAuth2Redirect | — | Google OAuth callback (reads `?token=`, logs in) |
| `/reservations` | ReservationForm | requiresAuth | `?vehicleId=` from "Rent now" |
| `/my-reservations` | MyReservations | requiresAuth | list + cancel |
| `/favorites` | Favorites | requiresAuth | list + remove |
| `/my-rentals` | RentalHistory | requiresAuth | timeline + document upload |
| `/my-invoices` | InvoiceList | requiresAuth | |
| `/my-invoices/:id` | InvoiceDetail | requiresAuth | |
| `/notifications` | Notifications | requiresAuth | inbox |
| `/profile` | Profile | requiresAuth | tabs: info / history / reviews / favorites |
| `/dashboard` → BackLayout | | requiresAuth + roles [ADMIN, MANAGER, STAFF] | sidebar shell |
| `/dashboard` `` | Dashboard | roles | stat cards |
| `/dashboard/vehicles` | VehicleManagement | roles | CRUD table + modal |
| `/dashboard/locations` | LocationManagement | roles | CRUD table + modal |
| `/:pathMatch(.*)*` | NotFound | — | 404 |

> **Gap:** `AppSidebar` lists items for `/dashboard/reservations`, `/rentals`, `/customers`,
> `/discounts`, `/invoices`, `/reviews`, `/notifications`, `/maintenance`, `/services`,
> `/audit-logs`, `/login-history` — but **only `vehicles` and `locations` child routes exist**.
> Clicking them falls through to the 404 page. These admin CRUD pages are the biggest remaining
> build item (see "To do next"). Roles gating: customers/discounts → ADMIN, audit-logs/
> login-history → ADMIN/MANAGER, rest → any staff role.

### Frontend conventions

- **App shell:** `App.vue` renders `<AppHeader />` globally; public pages need no layout wrapper.
  Admin area uses `BackLayout` (AppSidebar + header w/ NotificationBell + `<RouterView />`)
  via nested `/dashboard` children.
- **Service layer:** per-domain files in `services/` (`vehicles.js`, `reservations.js`, …), all
  through the shared axios instance. Feature-specific helpers (normalizers, price calculation,
  status timelines) live in the same service file.
- **Shared UI:** `components/base/` for primitives; `components/ui/` for DataTable/Modal;
  `components/layout/` for header/sidebar/notifications; domain components under
  `components/<domain>/` (VehicleCard, StarRating, ReviewList, ReviewForm).
- **Pages** live under `pages/<feature>/` named after their route (`MyReservations.vue` ↔
  `/my-reservations`).
- **Auth:** `stores/auth.store.js` (login/logout/isAuthenticated/hasRole/defaultRedirect).
  `services/api.js` attaches `Authorization: Bearer` and redirects on 401. Guards enforce
  `requiresAuth` + `roles`; the `/oauth2/redirect` page decodes the JWT from `?token=` (see
  `OAuth2AuthenticationSuccessHandler` on the backend).
- Firebase-style DTO access is defensive: services expose `normalizeVehicle*`,
  `normalizeImage`, `normalizeReview` because several response field names (e.g.
  `pricePerDay` vs `dailyRate`, `carType` vs `type`) were assumed, not confirmed. Verify exact
  DTO shapes via Swagger before relying on them.
- **Design language** is consistent across pages (see §4). Keep it that way — don't introduce a
  new look.

---

## 4. Frontend Design System

### Brand Colors

| Token | Hex | Usage |
|-------|-----|-------|
| `primary` | `#3D5FE0` | Buttons, links, active states, brand accent |
| `primary-hover` | `#3350C0` | Button/link hover state |
| `primary-light` | `#E9EDFB` | Avatar backgrounds, active sidebar items, icon containers |
| `dark` | `#1A2036` | Headings, text, dark card backgrounds |
| `gray-50/input-bg` | `#F3F4F6` | Input backgrounds, pill shapes, subtle fills |
| `gray-100/hover-bg` | `#F9FAFB` | Hover backgrounds, table header |
| `gray-200/border` | `#E5E7EB` | Borders, dividers |
| `gray-400/muted` | `#9CA3AF` | Placeholder text, muted icons, uppercase labels |
| `gray-500/secondary` | `#6B7280` | Secondary text, descriptions |
| `white` | `#FFFFFF` | Card backgrounds, primary surface |
| `red-600` | `#DC2626` | Error text, delete actions, notification badge |
| `red-50` | `#FEF2F2` | Error banner background, cancelled badge |
| `red-500/EF` | `#EF4444` | Favorite heart fill |
| `green-50` | `#F0FDF4` | Completed/paid badge background |
| `green-500` | `#22C55E` | Success states |

### Typography

- **Headings:** `text-[#1A2036]` bold. Page titles `text-3xl` (auth/customer pages) or
  `text-lg`/`text-xl` header titles; dashboard child pages use `text-xl`/`text-2xl`.
- **Body:** `text-sm`, primary `text-[#1A2036]`, secondary `text-[#6B7280]`.
- **Labels/uppercase:** `text-xs font-semibold uppercase text-[#9CA3AF]`.
- **Links:** `text-sm font-semibold text-[#3D5FE0] hover:text-[#3350C0]`.

### Component Patterns

**AppHeader (top nav, global)** — `components/layout/AppHeader.vue`:
- Left: brand "CarRental" → `/home`. Center nav: Home / Explore, + Favorites / My Reservations /
  My Rentals when logged in. Right: NotificationBell + Login button (guest) or Logout (user).
- Mobile: second horizontal scroll row of links.

**AppSidebar (admin)** — `components/layout/AppSidebar.vue`:
- Fixed `w-64` left column, brand link, `RouterLink` list w/ `active-class="!bg-[#E9EDFB] !text-[#3D5FE0]"`,
  role-gated items, bottom user block (avatar initials + email + role) + Logout button.

**BackLayout** — `layouts/BackLayout.vue`:
- `flex h-screen` + `bg-[#F9FAFB]`; `<AppSidebar />` + right column (header `h-16` white with
  page title derived from route path + `<NotificationBell />`) + scrollable `<main>`.
- Note: the header title is derived from the last URL segment — child pages should keep paths
  that read well (e.g. `/dashboard/vehicles` → "Vehicles").

**NotificationBell** — `components/layout/NotificationBell.vue`:
- Bell icon → `/notifications`, red badge `min-w-[1rem]` with unread count (`9+` cap), fetched
  from `notificationsApi.inbox()` on mount, non-fatal on error.

**DataTable** — `components/ui/DataTable.vue`:
- `columns=[{key,label}]`, `rows`, `loading`. Cell override via `#cell-${key}` slot,
  `#actions` slot with `:row`. Uses scoped resolve `a.b.c`. Empty/Loading states built in.

**Modal** — `components/ui/Modal.vue`:
- `<Teleport to="body">`, `open` + `title` props, `close` emit, backdrop click closes,
  `max-w-lg` white card.

**VehicleCard** — `components/vehicles/VehicleCard.vue`:
- Gradient header (`from-[#1A2036] to-[#3D5FE0]`), type badge, favorite heart (emits
  `toggle-favorite`), car SVG placeholder, name/price/specs + "Rent now" (emits `rent`).

**Reviews** — `components/reviews/StarRating.vue`, `ReviewList.vue`, `ReviewForm.vue`:
- Star rating input/display, paginated list, create/edit form.

### Design recipes (still the standard)

```html
<!-- Pill input -->
<div class="flex items-center gap-3 rounded-full bg-[#F3F4F6] px-5 py-3.5">
  <svg class="h-5 w-5 shrink-0 text-[#9CA3AF]">...</svg>
  <input class="w-full bg-transparent text-sm text-[#1A2036] placeholder:text-[#9CA3AF] outline-none" />
</div>

<!-- Primary pill button -->
<button class="w-full rounded-full bg-[#3D5FE0] py-3.5 text-sm font-semibold text-white transition hover:bg-[#3350C0] disabled:opacity-50">
</button>

<!-- Card -->
<article class="overflow-hidden rounded-2xl border border-[#E5E7EB]">
  <div class="p-4">...</div>
</article>

<!-- Error banner -->
<div class="rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-600">...</div>

<!-- Status badge -->
<span class="rounded-full px-2.5 py-0.5 text-xs font-semibold bg-[#E9EDFB] text-[#3D5FE0]">...</span>

<!-- Loading skeleton -->
<div class="h-28 animate-pulse rounded-2xl bg-[#F3F4F6]"></div>
```

---

## 5. Page Status Overview

| Page | Path | Status | Notes |
|------|------|--------|-------|
| Home | `/home` | ✅ Live data | fetches `/vehicles`, favorites aware |
| Explore | `/explore` | ✅ | client-side search + type filter |
| Vehicle detail | `/vehicles/:id` | ✅ | gallery, specs, status, price, favorites, reviews |
| Reservation form | `/reservations` | ✅ | price breakdown helper, locations/services/discounts |
| My reservations | `/my-reservations` | ✅ | list + cancel, status badges |
| Favorites | `/favorites` | ✅ | |
| Rental history | `/my-rentals` | ✅ | active/completed groups, timeline, doc upload |
| Invoices | `/my-invoices` (+ `/my-invoices/:id`) | ✅ | Bakong QR wiring marked TODO |
| Notifications | `/notifications` | ✅ | |
| Profile | `/profile` | ✅ | tabs: info, login history, my reviews |
| Auth (login/register/forgot/reset/oauth) | `/auth/*`, `/oauth2/redirect` | ✅ | all wired |
| Dashboard home | `/dashboard` | ✅ | client-side stat cards |
| Vehicle mgmt | `/dashboard/vehicles` | ✅ | DataTable + Modal CRUD |
| Location mgmt | `/dashboard/locations` | ✅ | DataTable + Modal CRUD |
| Reservations mgmt | `/dashboard/reservations` | ⬜ | sidebar link, route missing |
| Rentals mgmt | `/dashboard/rentals` | ⬜ | sidebar link, route missing |
| Customers mgmt | `/dashboard/customers` | ⬜ | sidebar link (ADMIN), route missing |
| Discounts mgmt | `/dashboard/discounts` | ⬜ | sidebar link (ADMIN), route missing |
| Invoices mgmt | `/dashboard/invoices` | ⬜ | sidebar link, route missing |
| Reviews mgmt | `/dashboard/reviews` | ⬜ | sidebar link, route missing |
| Notifications mgmt | `/dashboard/notifications` | ⬜ | sidebar link, route missing |
| Maintenance mgmt | `/dashboard/maintenance` | ⬜ | sidebar link, route missing |
| Services mgmt | `/dashboard/services` | ⬜ | sidebar link, route missing |
| Audit logs | `/dashboard/audit-logs` | ⬜ | sidebar link (ADMIN/MANAGER), route missing |
| Login history | `/dashboard/login-history` | ⬜ | sidebar link (ADMIN/MANAGER), route missing |
| FrontLayout | `layouts/FrontLayout.vue` | 🟡 unused | placeholder only; App.vue already renders global header |
| layouts/AppHeader.vue | `src/layouts/AppHeader.vue` | 🟡 duplicate | unused copy of components/layout/AppHeader.vue |

---

## 6. "To do next" / Recommended next steps

### Backend (API completeness)

- [x] **AuditLog feature:** model, repository, service (+impl), controller, `AuditActionEnum`,
      response DTO — implemented, `@PreAuthorize`-protected, verified `200 OK` via Swagger UI.
- [x] **pom.xml cleanup:** removed duplicate `spring-boot-starter-validation` / `lombok`
      declarations and invalid artifact IDs.
- [x] **Method security:** confirmed `@EnableMethodSecurity` on `SecurityConfig`.
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
      summaries (see `info.md` §11) are listed but not yet implemented. The dashboard currently
      computes stats client-side.
- [ ] **Notifications:** hook up real notification delivery (email / DB records) for booking
      confirmed/cancelled, payment, and rental reminders.
- [ ] **API docs polish:** ensure DTO examples and auth annotations render well in Swagger.
- [ ] **Wire AuditLog writes:** call `auditLogService.log(...)` from mutating service methods
      (create/update/delete on Vehicle, Reservation, Rental, User, etc.) — endpoints exist but
      nothing writes to it yet.
- [ ] **Confirm DTO field names** used by the frontend services (see services' normalize
      functions) so frontend assumptions match the actual response JSON.
- [ ] **Vehicle/other list filtering:** Explore filters client-side because `/api/vehicles`
      has no documented query params — adding server-side `?q=&type=&minPrice=...` would scale.

### Frontend — remaining work

#### Phase 1: Admin management pages (biggest gap) — sidebar points at 11 routes that 404

Pattern to copy (already established in `VehicleManagement.vue` / `LocationManagement.vue`):
DataTable + Modal + create/edit/delete via the shared axios `api`. Register each as a new child
route under the `/dashboard` BackLayout block in `router/index.js`.

- [ ] **Reservations** `/dashboard/reservations` — list all (`GET /api/reservations`), change
      status (`PATCH /api/reservations/{id}/status?status=`).
- [ ] **Rentals** `/dashboard/rentals` — list all (`GET /api/rentals`), advance lifecycle
      (`PATCH /api/rentals/{id}/status`).
- [ ] **Customers** `/dashboard/customers` (ADMIN) — list/activate/deactivate users from
      `GET /api/users`, role management.
- [ ] **Discounts** `/dashboard/discounts` (ADMIN) — CRUD `GET/POST/PUT/DELETE /api/discounts`.
- [ ] **Invoices** `/dashboard/invoices` — list all (`GET /api/invoices`), status management.
- [ ] **Reviews** `/dashboard/reviews` — list all (`GET /api/reviews`), delete inappropriate.
- [ ] **Notifications mgmt** `/dashboard/notifications` — send notifications to users
      (`POST /api/notifications/{userId}/notify`).
- [ ] **Maintenance** `/dashboard/maintenance` — CRUD maintenance records.
- [ ] **Services** `/dashboard/services` — CRUD add-on services.
- [ ] **Audit Logs** `/dashboard/audit-logs` (ADMIN/MANAGER) — `GET /api/admin/audit-logs`.
- [ ] **Login History** `/dashboard/login-history` (ADMIN/MANAGER) —
      `GET /api/admin/login-history`.

#### Phase 2: Cleanup

- [ ] **Delete duplicate** `src/layouts/AppHeader.vue` — `App.vue` already uses
      `components/layout/AppHeader.vue`; keeping two drifts.
- [ ] **FrontLayout.vue** — either implement (header/footer used by a layout route) or delete;
      it's currently an unused placeholder because the header is global.
- [ ] **Invoice payment wiring** — confirm Bakong request/response shapes against
      `BakongController` and finish QR + payment polling on `InvoiceDetail.vue` / `InvoiceList.vue`.
- [ ] **Vehicle images on cards** — `VehicleCard.vue` still shows the SVG placeholder; wire
      `fetchVehicleImages` per vehicle (batch) once image URLs are confirmed.
- [ ] **Confirm `Home.vue` vehicles** actually render images once image DTO confirmed.
- [ ] **Root redirect** `/` → `/preview` → change to `/home` for production.

#### Phase 3: Polish

- [ ] Toast/notification feedback for mutations (create/delete/upload) — currently inline errors
      + no success feedback.
- [ ] Empty/loading/error states consistency audit across all pages.
- [ ] Form validation improvements (server error mapping, field-level errors).

### Cross-cutting

- [ ] Environment docs: finalize `.env.example` with real keys and document running both servers
      together locally.
- [ ] Deployment: verify Spring profile for production and Vercel SPA rewrite (`vercel.json`)
      points at the correct API origin; backend OAuth success handler's redirect target.

---

## Notes for agents

- Don't add a new architectural layer or dependency unless the task actually needs it.
- Keep controller methods thin; business logic belongs in the service layer.
- When adding a new resource (e.g. `Booking`), create matching files across `model`, `enums`
  (if needed), `repository`, `dto/request/<resource>`, `dto/response/<resource>`, `mapper`,
  `service` (+ `impl`), and `controller` — don't skip the DTO/mapper layer.
- Follow existing naming: `<Resource>Controller`, `<Resource>Service`, `<Resource>ServiceImpl`,
  `<Resource>Repository`, `<Resource>RequestDTO`, `<Resource>ResponseDTO`.
- Frontend: put network calls in `services/<feature>.js`, shared UI in `components/ui` and
  `components/layout`, pages under `pages/<feature>/`. Match the existing design system (§4).
- Backend: run `./mvnw test` before considering a change complete.
- Frontend: run `npm run build` (or at least `npm run dev`) to verify changes compile.