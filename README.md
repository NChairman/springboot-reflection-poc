# Spring Boot Reflection Parameter Names POC

This proof-of-concept demonstrates how Spring Boot 3+ (Spring Framework 6.x) handles reflection parameter names, and why enabling the `-parameters` compiler flag is critical.

## Overview
Spring 6.1 removed `LocalVariableTableParameterNameDiscoverer`, which used bytecode analysis (debug info) to determine method parameter names. It now relies solely on standard Java reflection (`StandardReflectionParameterNameDiscoverer`) when parameter names are not explicitly provided in annotations.

This project uses two feature branches to illustrate the failure and success scenarios:

### 1. Error Scenario (`feature-reflection-param-error`)
Demonstrates the **runtime failure** when code is compiled without `-parameters`. Endpoints relying on implicit names will fail.
[Read detailed explanation](READMEs/reflection-error.md)

### 2. Success Scenario (`feature-reflection-param-success`)
Demonstrates the **fix** by enabling `-parameters` in the build configuration. Endpoints relying on implicit names will work correctly.
[Read detailed explanation](READMEs/reflection-success.md)

## All Endpoints

| Endpoint | Method | Parameters | Description |
|----------|--------|------------|-------------|
| `/demo/fail` | GET | `query` (required), `filter` (optional) | Relies on reflection for param names; fails without `-parameters`. |
| `/demo/success` | GET | `query` (required), `filter` (optional) | Uses explicit `@RequestParam(name = "...")`; works on both branches. |
| `/demo/fail/{id}` | GET | path: `id` | Relies on reflection for path variable name; fails without `-parameters`. |
| `/demo/success/{id}` | GET | path: `id` | Uses explicit `@PathVariable(name = "id")`; works on both branches. |

## Testing All Endpoints and Expected Outcomes

When you test, use the tables below. Base URLs: **Error branch** `http://localhost:8081` (or `http://localhost:8080` when running manually); **Success branch** `http://localhost:8082` (or `http://localhost:8080` when running manually).

### On `feature-reflection-param-error` (Error scenario)

| Endpoint | Example request | Expected outcome |
|----------|-----------------|------------------|
| `/demo/fail` | `GET /demo/fail?query=hello` | **Fails** — 400 Bad Request or 500 (parameter name discovery fails; `query` not bound). |
| `/demo/fail` | `GET /demo/fail?query=hello&filter=world` | **Fails** — same as above. |
| `/demo/success` | `GET /demo/success?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/success` | `GET /demo/success?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/fail/123` | `GET /demo/fail/123` | **Fails** — 400/500 (path variable name discovery fails). |
| `/demo/success/123` | `GET /demo/success/123` | **200 OK** — body: `id=123`. |

### On `feature-reflection-param-success` (Success scenario)

| Endpoint | Example request | Expected outcome |
|----------|-----------------|------------------|
| `/demo/fail` | `GET /demo/fail?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/fail` | `GET /demo/fail?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/success` | `GET /demo/success?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/success` | `GET /demo/success?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/fail/123` | `GET /demo/fail/123` | **200 OK** — body: `id=123`. |
| `/demo/success/123` | `GET /demo/success/123` | **200 OK** — body: `id=123`. |

## How to Run the Demo

### Using Docker Compose
Run both scenarios side-by-side using the deployment script:

```bash
./deploy-demo.sh
```

- **Error App**: `http://localhost:8081` — test all endpoints above; `/demo/fail` and `/demo/fail/{id}` fail; `/demo/success` and `/demo/success/{id}` succeed.
- **Success App**: `http://localhost:8082` — all four endpoints return 200 with the expected response bodies.

### Manual Verification
Switch branches and run tests, then run the app and hit the endpoints:

```bash
git checkout feature-reflection-param-error
./gradlew test
./gradlew bootRun
# Test all endpoints per the "On feature-reflection-param-error" table above.

git checkout feature-reflection-param-success
./gradlew test
./gradlew bootRun
# Test all endpoints per the "On feature-reflection-param-success" table above.
```
