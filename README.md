# Spring Boot Reflection Demo (Parameter Names)

A minimal demo showing how **Spring Framework 3+** (and Spring Boot 3+, which use Spring 6.x) handle controller parameter names, and why endpoints can fail at runtime when parameter names are not available.

## What This Demo Shows

- **Fail case**: Controller methods that use `@RequestParam` or `@PathVariable` **without** an explicit `name` (or `value`) rely on the framework to discover parameter names. If the code was not compiled with the `-parameters` compiler flag, name discovery fails and you get a **runtime error** when calling the endpoint.
- **Success case**: Using explicit names, e.g. `@RequestParam(name = "query")` or `@PathVariable(name = "id")`, works regardless of compiler settings and avoids the need for parameter name discovery.

## Why This Happens

From **Spring Framework 6.1** onward:

- **`LocalVariableTableParameterNameDiscoverer` has been removed.**  
  Spring no longer discovers parameter names by parsing bytecode (the old “debug info” / local variable table approach).
- Parameter names are now expected to come from:
  - **Reflection**, when the class was compiled with the **`-parameters`** compiler flag (Java 8+), so they are available to `StandardReflectionParameterNameDiscoverer`, or
  - **Explicit names** on the annotations (e.g. `@RequestParam(name = "query")`, `@PathVariable(name = "id")`).

If you compile **without** `-parameters` and do **not** specify names on `@RequestParam` / `@PathVariable`, Spring cannot bind request parameters or path variables correctly and you get runtime failures (e.g. missing required parameter, or wrong binding).

This affects any use case that depends on parameter names: dependency injection, property binding, SpEL expressions, and **controller method parameters** (e.g. `@RequestParam`, `@PathVariable`, `@RequestHeader`).

References:

- [Spring Framework 6.1 Release Notes – Parameter Name Retention](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-6.1-Release-Notes#parameter-name-retention)
- [Upgrading to Spring Framework 6.x](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x)

## Endpoints in This Project

| Endpoint              | Behavior |
|-----------------------|----------|
| `GET /demo/fail?query=...`       | Uses `@RequestParam String query` (no explicit name). **Fails at runtime** if the project is compiled without `-parameters`. |
| `GET /demo/fail/{id}`            | Uses `@PathVariable String id` (no explicit name). **Fails at runtime** if compiled without `-parameters`. |
| `GET /demo/success?query=...`    | Uses `@RequestParam(name = "query")`. **Works** regardless of `-parameters`. |
| `GET /demo/success/{id}`         | Uses `@PathVariable(name = "id")`. **Works** regardless of `-parameters`. |

## How to Reproduce the Failure

1. **Ensure the project is compiled without `-parameters`**  
   In this repo, the Gradle build is intentionally left **without** the `-parameters` flag so that the “fail” endpoints demonstrate the issue.

2. **Run the application**  
   ```bash
   ./gradlew bootRun
   ```

3. **Call the fail endpoint** (e.g. with `curl`):
   ```bash
   curl "http://localhost:8080/demo/fail?query=test"
   ```
   You should see a **runtime error** (e.g. 500 or failure to resolve the parameter), because Spring cannot discover the parameter name `query`.

4. **Call the success endpoint**:
   ```bash
   curl "http://localhost:8080/demo/success?query=test"
   ```
   This should return a normal response, because the name is given explicitly in the annotation.

## How to Fix It

You can fix the issue in one of two ways.

### Option 1: Use explicit names (code change)

Always specify the name in the annotation so the framework does not need to discover it:

```java
// Request params
@RequestParam(name = "query") String q
@RequestParam(name = "filter", required = false) String f

// Path variables
@PathVariable(name = "id") String resourceId
```

This works on any Java version and with any compiler settings, and is the most reliable approach for controllers.

### Option 2: Compile with `-parameters` (build change)

Compile your Java sources with the `-parameters` flag so that parameter names are available via reflection. Then `@RequestParam String query` (without `name`) can still work.

**Gradle (Groovy DSL):**

```groovy
tasks.withType(JavaCompile).configureEach {
    options.compilerArgs.add("-parameters")
}
```

**Gradle (Kotlin DSL):**

```kotlin
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}
```

**Maven:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

After adding `-parameters` and recompiling, the “fail” endpoints in this demo will also work, because Spring can then use `StandardReflectionParameterNameDiscoverer` to get the parameter names.

**Note:** If you use Java agents that modify bytecode, they can strip parameter information; this was addressed in JDK 19. Prefer a recent JDK if you rely on `-parameters`.

## Migration checklist (upgrading from an older Spring version)

When moving from Spring Framework 5.x / Spring Boot 2.x to Spring Framework 6.x / Spring Boot 3+:

1. **Identify usages that depend on parameter names**  
   - `@RequestParam`, `@PathVariable`, `@RequestHeader` without explicit `name`/`value`  
   - Constructor injection and other places that rely on parameter name discovery

2. **Choose your strategy**  
   - **Explicit names**: Add `name` (or `value`) to every such annotation in controllers (and other affected code).  
   - **Or** enable **`-parameters`** in your build and in your IDE so that existing “implicit name” code keeps working.

3. **Enable `-parameters` in the build**  
   If you rely on implicit names, add the `-parameters` flag to the Java compiler in Gradle or Maven as shown above.

4. **Align your IDE**  
   - **IntelliJ**: Settings → Build, Execution, Deployment → Compiler → Java Compiler → “Additional command line parameters” → add `-parameters`.  
   - **Eclipse**: Preferences → Java → Compiler → enable “Store information about method parameters (usable via reflection)”.

5. **Run tests and manual checks**  
   Call all affected endpoints and flows; without `-parameters` or explicit names, binding can fail at runtime with unclear errors.

## Running the application

```bash
./gradlew bootRun
```

Then use the endpoints above (e.g. with `curl`) to compare `/demo/fail` and `/demo/success`.
