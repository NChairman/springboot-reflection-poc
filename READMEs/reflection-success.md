# Reflection Parameter Names Demo: Success Scenario

This branch (`feature-reflection-param-success`) demonstrates the **fix** for reflection parameter name discovery issues in Spring Framework 6.x.

By enabling the `-parameters` compiler flag in `build.gradle`, Spring can discover parameter names via standard Java reflection (`StandardReflectionParameterNameDiscoverer`).

## All Endpoints on This Branch

On the **success** branch only, all four endpoints return 200 OK. On the error branch, `/demo/fail` and `/demo/fail/{id}` fail with **500 Internal Server Error**. Here, with `-parameters` enabled, those paths succeed and return 200.

| Endpoint | Method | Parameters | Expected outcome on this branch |
|----------|--------|------------|---------------------------------|
| `/demo/fail` | GET | `query` (required), `filter` (optional) | **200 OK** — body: `query=<value>, filter=<value>`. |
| `/demo/success` | GET | `query` (required), `filter` (optional) | **200 OK** — body: `query=<value>, filter=<value>`. |
| `/demo/fail/{id}` | GET | path: `id` | **200 OK** — body: `id=<value>`. |
| `/demo/success/{id}` | GET | path: `id` | **200 OK** — body: `id=<value>`. |

## Testing All Endpoints and Expected Outcomes

When you test on `feature-reflection-param-success`, use the following. Base URL: `http://localhost:8080` (or `http://localhost:8082` if using Docker). **All** endpoints should return 200 with the response bodies below.

| Endpoint | Example request | Expected outcome |
|----------|-----------------|------------------|
| `/demo/fail` | `GET /demo/fail?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/fail` | `GET /demo/fail?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/success` | `GET /demo/success?query=hello` | **200 OK** — body: `query=hello, filter=null`. |
| `/demo/success` | `GET /demo/success?query=hello&filter=world` | **200 OK** — body: `query=hello, filter=world`. |
| `/demo/fail/123` | `GET /demo/fail/123` | **200 OK** — body: `id=123`. |
| `/demo/success/123` | `GET /demo/success/123` | **200 OK** — body: `id=123`. |

## How to Verify
1.  Check `build.gradle`: Ensure `-parameters` is added to `compileJava`.
    ```groovy
    tasks.withType(JavaCompile).configureEach {
        options.compilerArgs.add("-parameters")
    }
    ```
2.  Run tests: `./gradlew test`.
    *   Tests pass *because all endpoints work correctly*.
3.  Run the app: `./gradlew bootRun`.
    *   Test **all** endpoints above and confirm each returns 200 with the expected response body.

## Why it Works
The `-parameters` compiler flag instructs the Java compiler to include `MethodParameters` metadata in the generated class file. Spring 6.1+ uses this metadata to discover parameter names (e.g. `query`, `filter`) and bind them correctly.
