# Reflection Parameter Names Demo: Error Scenario

This branch (`feature-reflection-param-error`) demonstrates the **runtime failure** when reflection parameter name discovery is not configured correctly in Spring Framework 6.x.

Because the `-parameters` compiler flag is **disabled** (removed from build options), Spring cannot discover parameter names for endpoints that rely on them (i.e., those without explicit `name` attributes in `@RequestParam` or `@PathVariable`).

## All Endpoints on This Branch

| Endpoint | Method | Parameters | Expected outcome on this branch |
|----------|--------|------------|---------------------------------|
| `/demo/fail` | GET | `query` (required), `filter` (optional) | **Fails** (400/500 — param name discovery fails). |
| `/demo/success` | GET | `query` (required), `filter` (optional) | **200 OK** — body: `query=<value>, filter=<value>`. |
| `/demo/fail/{id}` | GET | path: `id` | **Fails** (400/500 — path variable name discovery fails). |
| `/demo/success/{id}` | GET | path: `id` | **200 OK** — body: `id=<value>`. |

## Testing All Endpoints and Expected Outcomes

When you test on `feature-reflection-param-error`, use the following. Base URL: `http://localhost:8080` (or `http://localhost:8081` if using Docker).

| Endpoint | Example request | Expected outcome |
|----------|-----------------|------------------|
| `/demo/fail` | `GET /demo/fail?query=hello` | **Fails** — 400 Bad Request or 500 (parameter name discovery fails). |
| `/demo/fail` | `GET /demo/fail?query=hello&filter=world` | **Fails** — same as above. |
| `/demo/success` | `GET /demo/success?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/success` | `GET /demo/success?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/fail/123` | `GET /demo/fail/123` | **Fails** — 400/500 (path variable name discovery fails). |
| `/demo/success/123` | `GET /demo/success/123` | **200 OK** — body: `id=123`. |

## How to Verify
1.  Check `build.gradle`: Ensure `-parameters` is removed from `compileJava`.
2.  Run tests: `./gradlew test`.
    *   Tests pass *because they expect the failing endpoints to throw exceptions*.
3.  Run the app: `./gradlew bootRun`.
    *   Test **all** endpoints above and confirm the expected outcomes (fail for `/demo/fail` and `/demo/fail/{id}`; success for `/demo/success` and `/demo/success/{id}`).

## Why it Fails
Spring 6.1+ removed `LocalVariableTableParameterNameDiscoverer`. It now relies on `StandardReflectionParameterNameDiscoverer`, which requires method parameter names to be present in the compiled class file. Without `-parameters`, these names are missing (e.g. `arg0`, `arg1`), so Spring cannot bind `query` to the correct argument.
