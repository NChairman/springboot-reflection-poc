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

## How to Run the Demo

### Using Docker Compose
Run both scenarios side-by-side using the deployment script:

```bash
./deploy-demo.sh
```

- **Error App**: `http://localhost:8081/demo/fail` (Fails)
- **Success App**: `http://localhost:8082/demo/fail` (Succeeds)

### Manual Verification
Switch branches to see the code changes and test results:

```bash
git checkout feature-reflection-param-error
./gradlew test

git checkout feature-reflection-param-success
./gradlew test
```
