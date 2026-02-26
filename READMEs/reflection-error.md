# Reflection Parameter Names Demo: Error Scenario

This branch (`feature-reflection-param-error`) demonstrates the **runtime failure** when reflection parameter name discovery is not configured correctly in Spring Framework 6.x.

Because the `-parameters` compiler flag is **disabled** (removed from build options), Spring cannot discover parameter names for endpoints that rely on them (i.e., those without explicit `name` attributes in `@RequestParam` or `@PathVariable`).

## How to Verify
1.  Check `build.gradle`: Ensure `-parameters` is removed from `compileJava`.
2.  Run tests: `./gradlew test`.
    *   Tests pass *because they expect the endpoints to throw exceptions*.
3.  Run the app: `./gradlew bootRun`.
    *   Call `/demo/fail?query=test`.
    *   Observe a runtime error (e.g. 500 or 400 with missing parameter).

## Why it Fails
Spring 6.1+ removed `LocalVariableTableParameterNameDiscoverer`. It now relies on `StandardReflectionParameterNameDiscoverer`, which requires method parameter names to be present in the compiled class file. Without `-parameters`, these names are missing (e.g. `arg0`, `arg1`), so Spring cannot bind `query` to the correct argument.
