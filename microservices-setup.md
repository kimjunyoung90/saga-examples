# Saga Pattern Microservices Setup

This document provides an overview of the microservice architecture setup for implementing saga patterns.

## Microservices Overview

| Service | Port | Description | Database | Endpoint Base |
|---------|------|-------------|----------|---------------|
| Order Service | 8081 | Manages order lifecycle | H2 (orderdb) | http://localhost:8081 |
| Payment Service | 8082 | Handles payment processing | H2 (paymentdb) | http://localhost:8082 |
| Inventory Service | 8083 | Manages product inventory | H2 (inventorydb) | http://localhost:8083 |

## Quick Start

### Build All Services
```bash
# Build individual services
cd order && ./gradlew build
cd ../payment && ./gradlew build  
cd ../inventory && ./gradlew build
```

### Run Services
```bash
# Terminal 1 - Order Service
cd order && ./gradlew bootRun

# Terminal 2 - Payment Service  
cd payment && ./gradlew bootRun

# Terminal 3 - Inventory Service
cd inventory && ./gradlew bootRun

```

### Health Checks
Once running, verify services are healthy:
- Order Service: http://localhost:8081/actuator/health
- Payment Service: http://localhost:8082/actuator/health
- Inventory Service: http://localhost:8083/actuator/health

## Database Access
Each service includes H2 console for database inspection:
- Order Service: http://localhost:8081/h2-console
- Payment Service: http://localhost:8082/h2-console
- Inventory Service: http://localhost:8083/h2-console

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:{servicename}db`
- Username: `sa`
- Password: `password`

## Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle 8.13
- **Java Version**: 17
- **Database**: H2 (in-memory)
- **Dependencies**:
  - spring-boot-starter-web
  - spring-boot-starter-actuator
  - spring-boot-starter-validation
  - spring-boot-starter-data-jpa

## Next Steps
1. Implement REST API endpoints for each service
2. Add domain models and repositories
3. Implement saga coordination (choreography or orchestration)
4. Add event publishing/consuming capabilities
5. Implement compensation logic for rollback scenarios