# Deployment Checklist — Spring Car Rental

Step-by-step guide for deploying the Spring Boot backend + Vue 3 frontend.
Everything Docker-related lives in this `deploy/` folder; the rest of the
repo is referenced here by path.

---

## 1. Environment variables

Copy every `.env.example` to its `.env` and fill in real values.
**Never commit the real `.env` files** (all are gitignored).

### Backend — `spring_backend/.env.example`
Required: `DB_HOST`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, `MAIL_USERNAME`,
`MAIL_PASSWORD`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`,
`FACEBOOK_CLIENT_ID`, `FACEBOOK_CLIENT_SECRET`, `OAUTH2_REDIRECT_URI`,
`CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`,
`BAKONG_ACCOUNT_ID`, `BAKONG_BASE_URL`, `EMAIL`.
Optional with defaults: `JWT_EXPIRE` (86400000), `APP_CORS_ALLOWED_ORIGINS`
(http://localhost:5173).

> **JWT secret:** must be at least 32 bytes. The service now **fails fast at
> startup** if `JWT_SECRET` is missing (no fallback value in source).
> Generate with `openssl rand -base64 48` — do **not** reuse the dev value.

### Frontend — `vue_frontend/.env.example`
Only `VITE_API_BASE_URL` is read by the app (defaults to `/api` at runtime;
Vite bakes it in at build time via the Docker `ARG`).

> **Cloudinary:** `VITE_CLOUDINARY_CLOUD_NAME`, `VITE_CLOUDINARY_UPLOAD_PRESET`
> and `VITE_CLOUDINARY_VEHICLE_PRESET` are **no longer needed** since Phase 3B-1
> (uploads go through `POST /api/uploads` on the backend). Leave them out of
> production builds. The **backend** still requires
> `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`.

### Docker Compose — `spring_backend/deploy/.env.example`
Same variables again, interpolated by `docker-compose.yml`. In `deploy/`:
`cp .env.example .env`.

---

## 2. Build (local sanity check)

```bash
# Backend  (Java 21 required; Bunneng machine: run on Windows where the JDK lives)
cd spring_backend
./mvnw clean install          # runs compile + package (+ tests if any)

# Frontend
cd vue_frontend
npm install                   # or: npm ci
npm run build                 # outputs to dist/
```

Both must complete with 0 errors.

---

## 3. Database setup / migration

- The app is configured with Hibernate `spring.jpa.hibernate.ddl-auto=update`
  (`spring_backend/src/main/resources/application.properties`).
- **`update` is fine for dev only. For production set it to `validate`**
  (schema must already exist) or `none` with real migrations (Flyway/Liquibase).
  Override without code changes via the env var:
  `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
- Also disable verbose SQL in production: `SPRING_JPA_SHOW_SQL=false`.
- With docker-compose, the `mysql` service auto-creates the `car_rental`
  database and persists data in the `mysql_data` volume. Fresh schema is
  created On first backend boot (because of `ddl-auto`).
- Backup strategy: snapshot the `mysql_data` volume (e.g.
  `docker run --rm -v car-rental-mysql_data:/data -v "$PWD":/backup alpine tar czf /backup/mysql_data.tgz -C /data .`).

---

## 4. Run with Docker Compose

```bash
cd spring_backend/deploy
cp .env.example .env          # fill everything in first
docker compose up -d --build
docker compose ps             # check health
docker compose logs -f backend
```

- Backend → `http://localhost:8080` (Swagger at `/swagger-ui/index.html`)
- Frontend → `http://localhost:80`
- `mysql` service runs inside the compose network at `mysql:3306`; the
  backend's `DB_HOST` is already set to that in the compose file.

---

## 5. CORS — must set for the real domain

Backend reads `APP_CORS_ALLOWED_ORIGINS` (comma-separated) with a default of
`http://localhost:5173`. In production set it in `deploy/.env` to your actual
frontend origin, e.g.:
```
APP_CORS_ALLOWED_ORIGINS=https://car-rental.example.com
```
If the frontend is served through the compose nginx at the **same domain** and
base URL is `/api`, the nginx proxy makes all API calls same-origin and CORS is
not even triggered — the value above only matters when the browser talks to a
different origin (e.g. a hosted frontend calling the backend directly).

---

## 6. Google OAuth redirect URIs (Google Cloud Console)

1. Open the OAuth 2.0 Client for this app in the Google Cloud Console.
2. Add the **backend callback** as an Authorized redirect URI:
   `https://<backend-domain>/login/oauth2/code/google`
3. If you host the frontend on another origin, that origin must be allowed in
   CORS (`APP_CORS_ALLOWED_ORIGINS`) so the OAuth flow's redirects/browser
   calls work.
4. Set `OAUTH2_REDIRECT_URI` in backend/deploy env to the frontend page that
   handles the post-login token, e.g. `https://car-rental.example.com/oauth2/redirect`.

Facebook follows the same pattern with `/login/oauth2/code/facebook`.

---

## 7. Rollback

- Tag images per release, e.g. `car-rental-backend:<git-sha>` and
  `car-rental-frontend:<git-sha>` when you build.
- To roll back, point compose at the previous known-good tag and recreate:
  ```bash
  cd spring_backend/deploy
  # edit docker-compose.yml to use image: car-rental-backend:<good-sha> (instead of build: ./../)
  docker compose up -d --force-recreate
  ```
- Alternatively revert the code commit and rebuild:
  ```bash
  git checkout <previous-good-commit>
  docker compose -f deploy/docker-compose.yml up -d --build
  ```
- **Database:** `ddl-auto=update` only adds/migrates schema; it does not drop
  data. If the rollback involves a schema change, restore the `mysql_data`
  volume backup (see §3) taken before the failed deploy.