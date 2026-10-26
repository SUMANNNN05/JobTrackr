# JobTrackr API

A production-style REST API for tracking job applications, moving them through a hiring pipeline, and analysing your funnel with SQL.

![CI](https://github.com/YOUR_USERNAME/jobtrackr/actions/workflows/ci.yml/badge.svg)

**Live demo:** `https://YOUR-APP.onrender.com/swagger-ui.html` (replace after deploying)

## Tech stack

| Area | Choice |
|------|--------|
| Language / runtime | Java 21 (virtual threads enabled) |
| Framework | Spring Boot 3.5, Spring Web, Spring Data JPA, Bean Validation |
| Security | Spring Security, stateless JWT auth, BCrypt |
| Database | PostgreSQL 16, Flyway migrations, indexed queries, native SQL with window functions |
| API docs | springdoc OpenAPI / Swagger UI, RFC 7807 problem+json errors |
| Observability | Actuator health probes, Micrometer + Prometheus metrics |
| Testing | JUnit 5, MockMvc, Testcontainers (real PostgreSQL) |
| DevOps | Multi-stage Dockerfile, Docker Compose, GitHub Actions (test, build, push to GHCR), Render Blueprint |

## Features

- Register / login with JWT
- CRUD for job applications, with pagination, sorting and status filtering
- Pipeline statuses: `WISHLIST > APPLIED > ONLINE_ASSESSMENT > INTERVIEW > OFFER` (or `REJECTED` / `WITHDRAWN`)
- Full status-change audit trail per application
- Analytics: funnel counts, interview and offer conversion rates, weekly applications with running total (SQL window function)
- Strict per-user data isolation (user A gets 404 on user B's records)

## Architecture

```
controller  ->  service  ->  repository  ->  PostgreSQL (Flyway-managed schema)
   |
JwtAuthFilter -> SecurityContext (stateless)
```

Packages: `auth`, `application` (core domain), `analytics`, `security`, `config`, `exception`, `user`.

## Run locally

```bash
# Option 1: everything in Docker
docker compose up --build

# Option 2: DB in Docker, app from Maven
docker compose up -d db
mvn spring-boot:run
```

Open http://localhost:8080/swagger-ui.html

```bash
# quick tour
TOKEN=$(curl -s -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"me@example.com","password":"password123","fullName":"Me"}' | jq -r .token)

curl -X POST localhost:8080/api/applications -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"company":"Acme","role":"Backend Engineer","status":"APPLIED","appliedOn":"2026-01-12"}'

curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/analytics/summary
```

## API overview

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register`, `/api/auth/login` | Get a JWT |
| POST | `/api/applications` | Create application |
| GET | `/api/applications?status=&page=&size=&sort=` | Paginated list |
| GET / PUT / DELETE | `/api/applications/{id}` | Read / update / delete |
| PATCH | `/api/applications/{id}/status` | Move through pipeline (recorded in history) |
| GET | `/api/applications/{id}/history` | Status timeline |
| GET | `/api/analytics/summary` | Funnel + conversion rates |
| GET | `/api/analytics/weekly` | Weekly counts + cumulative total |

## Tests

```bash
mvn verify      # needs Docker running (Testcontainers starts PostgreSQL)
```

## Deploy

### Render (free tier)
1. Push this repo to GitHub.
2. Render dashboard, **New > Blueprint**, select the repo. `render.yaml` provisions the web service and PostgreSQL and generates `JWT_SECRET`.
3. Wait for the build, then open `/swagger-ui.html` on your service URL.

Note: Render's free Postgres expires after 30 days and free web services sleep when idle. Recreate the DB, or move to Neon/Supabase (set `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).

### Any Docker host (Railway, Fly.io, Koyeb, AWS App Runner)
CI publishes `ghcr.io/<you>/jobtrackr:latest` on every push to `main`. Point the platform at that image and set the env vars from `.env.example`.

## Design decisions

- **Flyway owns the schema** (`ddl-auto: none`), so the DB is versioned and reproducible.
- **DTO records** keep entities out of the API layer.
- **Composite indexes** match the actual query patterns (`user_id, status`) and (`user_id, applied_on`).
- **Page size capped** at 100 to protect the database.
- **Virtual threads** give high concurrency for blocking JDBC without reactive complexity.
