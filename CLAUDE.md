# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a saga pattern examples repository demonstrating two distinct approaches to distributed transaction management:

- **choreography**: Basic Java implementation showcasing saga choreography pattern
- **orchestration**: Spring Boot application demonstrating saga orchestration pattern

## Architecture

### Module Structure
```
saga-examples/
├── choreography/          # Pure Java saga choreography example
│   └── src/main/java/org/example/Main.java
└── orchestration/         # Spring Boot saga orchestration example
    └── src/main/java/org/example/Main.java
```

### Key Patterns
- **Choreography Pattern**: Each service publishes and listens to events, managing its part of the saga independently
- **Orchestration Pattern**: A central orchestrator coordinates all the saga steps

## Development Commands

### Choreography Module
```bash
cd choreography
./gradlew build          # Build the project
./gradlew test           # Run tests
./gradlew clean          # Clean build artifacts
./gradlew jar            # Create JAR file
./gradlew check          # Run all verification tasks
```

### Orchestration Module
```bash
cd orchestration
./gradlew build          # Build the project
./gradlew test           # Run tests
./gradlew bootRun        # Run Spring Boot application
./gradlew bootJar        # Create executable JAR
./gradlew bootBuildImage # Create OCI image
./gradlew clean          # Clean build artifacts
./gradlew check          # Run all verification tasks
```

## Testing

Both modules use JUnit 5 for testing:
- Run tests: `./gradlew test`
- Test reports are generated in `build/reports/tests/`

## Build Configuration

- **choreography**: Standard Java project with JUnit 5
- **orchestration**: Spring Boot 3.2.0 with embedded Tomcat and test starter

## Key Dependencies

### Choreography
- Java platform
- JUnit 5 for testing

### Orchestration
- Spring Boot 3.2.0
- Spring Boot Starter Web (includes embedded Tomcat)
- Spring Boot Starter Test (includes JUnit 5, Mockito, AssertJ)

## IntelliJ IDEA Integration

This repository is configured as an IntelliJ IDEA project with:
- Gradle build system integration
- Module definitions for both choreography and orchestration
- Java toolchain support