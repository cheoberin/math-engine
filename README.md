# Math Engine

Math Engine is a Spring Boot service for defining configurable calculation layouts and evaluating numeric submissions against those layouts.

## What it does

- Manages **layouts** (calculation templates) with activation/deactivation.
- Manages **fields** in each layout:
  - `INPUT` fields (provided by clients in submissions)
  - `CALCULATION` fields (computed from formulas)
- Accepts submissions and processes them asynchronously.
- Returns computed values with standardized decimal scaling by field type.
- Exposes REST endpoints with OpenAPI/Swagger UI.

## Tech stack

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Flyway
- PostgreSQL
- exp4j (formula evaluation)

## Architecture

The project follows a ports-and-adapters (hexagonal) style:

- `domain` contains core models and business rules.
- `application` contains use cases/services and orchestration logic.
- `adapter/input/http` contains REST API contracts, controllers, DTOs, and exception mapping.
- `adapter/output/jpa` contains persistence entities, repositories, and adapters.
- `adapter/output/lib` contains external library integration (`exp4j`).

Key behavior:

- Layouts start as `INACTIVE` and must be activated before submissions are allowed.
- Field formulas use references like `[FIELD_KEY]`.
- Formula dependencies are validated (unknown references and circular dependencies are rejected).
- Calculation fields are evaluated by dependency order.
- Submission processing runs in a scheduled job (`fixedDelay = 10000`, up to 5 pending submissions per run).

## Formula syntax

- Field reference: `[A1]`
- Arithmetic: `+`, `-`, `*`, `/`
- Absolute value: `|expr|` (internally converted to `abs(expr)`)
- Safe division: division by zero returns `0`
- Missing variable values are treated as `0`

Example formula:

```text
([A1] + [B2]) / |[C3]|
```

## Field types and scaling

Processed values are rounded with `HALF_UP` according to field type:

- `NUMBER` -> scale 0
- `DECIMAL_2`, `DECIMAL_2_OPT`, `PERCENT`, `CURRENCY` -> scale 2
- `DECIMAL_4`, `DECIMAL_4_OPT`, `PERCENT_2` -> scale 4
- `PERCENT_4` -> scale 6

## Requirements

- JDK 21
- PostgreSQL (local or remote)
- Maven (or use included Maven Wrapper)

## Configuration

Main config files:

- `src/main/resources/application.yaml`
- `src/main/resources/application-local.yaml`

The app expects `SPRING_PROFILES_ACTIVE` to be set. For local development, use `local`.

### Local profile defaults

From `application-local.yaml`:

- JDBC URL: `jdbc:postgresql://localhost:5432/math-engine`
- Username: `postgres`
- Password: `postgres`
- Hibernate DDL: `validate`
- SQL logging enabled

### Management endpoints

Actuator endpoints are fully exposed (`management.endpoints.web.exposure.include=*`).

## Database migrations

Flyway migrations are in `src/main/resources/db/migration`:

- `V1__create-layout-table.sql`
- `V2__create-field-table.sql`
- `V3__add-name-column-to-field-table.sql`
- `V4__create-submission-tables.sql`
- `V5__create-processed-table.sql`

## Run locally (Windows PowerShell)

1) Set active profile
2) Run the app

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
.\mvnw.cmd spring-boot:run
```

The app starts on `http://localhost:8080`.

## Build and test

```powershell
.\mvnw.cmd clean verify
```

Run only tests:

```powershell
.\mvnw.cmd test
```

## Docker

Build image:

```powershell
docker build -t math-engine:local .
```

Run container (set DB env vars as needed for your environment):

```powershell
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker math-engine:local
```

> Note: the repository currently includes `application.yaml` and `application-local.yaml`. If you run with `docker`, ensure datasource-related properties are provided through environment variables or externalized config.

## API documentation

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## REST API overview

### Layouts

- `POST /api/layouts`
- `PUT /api/layouts/{id}`
- `GET /api/layouts/{id}`
- `GET /api/layouts?search=...`
- `PATCH /api/layouts/{id}/activate`
- `PATCH /api/layouts/{id}/deactivate`

### Fields

- `POST /api/fields`
- `PUT /api/fields/{id}`
- `GET /api/fields/{id}`
- `GET /api/fields/layout/{layoutId}?search=...`
- `DELETE /api/fields/{id}`
- `DELETE /api/fields/batch`

### Submissions

- `POST /api/submissions`
- `GET /api/submissions/{id}`

## API examples

### 1) Create a layout

```bash
curl -X POST http://localhost:8080/api/layouts \
  -H "Content-Type: application/json" \
  -d '{
    "externalKey": "L001",
    "name": "Loan Layout"
  }'
```

### 2) Activate the layout

```bash
curl -X PATCH http://localhost:8080/api/layouts/{layoutId}/activate
```

### 3) Create input fields

```bash
curl -X POST http://localhost:8080/api/fields \
  -H "Content-Type: application/json" \
  -d '{
    "externalKey": "AMT",
    "name": "Amount",
    "layout": "{layoutId}",
    "source": "INPUT",
    "formula": null,
    "fieldType": "DECIMAL_2"
  }'
```

```bash
curl -X POST http://localhost:8080/api/fields \
  -H "Content-Type: application/json" \
  -d '{
    "externalKey": "RATE",
    "name": "Rate",
    "layout": "{layoutId}",
    "source": "INPUT",
    "formula": null,
    "fieldType": "DECIMAL_4"
  }'
```

### 4) Create a calculated field

```bash
curl -X POST http://localhost:8080/api/fields \
  -H "Content-Type: application/json" \
  -d '{
    "externalKey": "INT",
    "name": "Interest",
    "layout": "{layoutId}",
    "source": "CALCULATION",
    "formula": "[AMT]*[RATE]",
    "fieldType": "DECIMAL_2"
  }'
```

### 5) Submit values

```bash
curl -X POST http://localhost:8080/api/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "layout": "L001",
    "fields": [
      { "fieldCode": "AMT", "value": 1000 },
      { "fieldCode": "RATE", "value": 0.075 }
    ]
  }'
```

### 6) Fetch results

```bash
curl http://localhost:8080/api/submissions/{submissionId}
```

If processing is not finished yet, this endpoint returns a conflict error. Retry after the next execution cycle.

## Error handling

Global error handling is implemented in `ControllerAdviser`:

- `404` for not found resources
- `409` for business conflicts (inactive layout, duplicate keys, not completed submission, etc.)
- `400` for validation errors
- `500` for unexpected errors (includes `traceId`)

For submission field mismatch errors, the response includes:

- `duplicateCodes`
- `missingCodes`
- `notExpectedCodes`

## Request tracing

Requests support `X-Trace-Id` header.

- If provided, it is reused.
- If missing, a UUID is generated.
- On unexpected errors, `traceId` is included in the response body.

## Project structure

```text
src/main/java/com/cheobs/math_engine
|- adapter
|  |- input/http
|  |- output/jpa
|  |- output/lib
|- application
|  |- service
|  |- jobs
|- domain
   |- model
   |- port
```

## Notes for contributors

- Keep formulas using bracketed field keys (for example `[AMT]`).
- When adding new calculation logic, preserve dependency validation and ordering semantics.
- Run tests before creating PRs.
