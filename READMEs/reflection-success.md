# Reflection Parameter Names Demo: Success Scenario

This branch (`feature-reflection-param-success`) demonstrates the **fix** for reflection parameter name discovery issues in Spring Framework 6.x.

By enabling the `-parameters` compiler flag in `build.gradle`, Spring can discover parameter names via standard Java reflection (`StandardReflectionParameterNameDiscoverer`).

## How to Verify
1.  Check `build.gradle`: Ensure `-parameters` is added to `compileJava`.
    ```groovy
    tasks.withType(JavaCompile).configureEach {
        options.compilerArgs.add("-parameters")
    }
    ```
2.  Run tests: `./gradlew test`.
    *   Tests pass *because the endpoints work correctly*.
3.  Run the app: `./gradlew bootRun`.
    *   Call `/demo/fail?query=test`.
    *   Observe a success response.

## Why it Works
The `-parameters` compiler flag instructs the Java compiler to include `MethodParameters` metadata in the generated class file. Spring 6.1+ uses this metadata to discover parameter names (e.g. `query`, `filter`) and bind them correctly.
