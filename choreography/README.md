# Choreography Saga Pattern

이 프로젝트는 Choreography 방식의 Saga Pattern 구현 예제 입니다.

## 아키텍처

```mermaid
flowchart BT
    subgraph subGraph0["Message Broker"]
        Kafka["Apache Kafka"]
    end
    subgraph Microservices["Microservices"]
        OS["Order Service"]
        PS["Payment Service"]
        IS["Inventory Service"]
    end
    Client["Client Application"] -- Request --> OS
    OS <-- Events --> Kafka
    PS <-- Events --> Kafka
    IS <-- Events --> Kafka

    style Kafka fill:#fff3e0
    style OS fill:#e1f5fe
    style PS fill:#e8f5e8
    style IS fill:#f3e5f5
```
- 각 서비스는 이벤트를 발행 및 구독하며 처리합니다.

## 구성 요소

- **Order Service** (Port 8081): 주문 생성 및 관리
- **Inventory Service** (Port 8082): 재고 관리
- **Payment Service** (Port 8083): 결제 처리
- **Kafka (KRaft)**: 서비스 간 이벤트 메시징


## 이벤트 흐름

### Success Flow (성공 시나리오)

```mermaid
sequenceDiagram
    participant Client
    participant OrderService
    participant Kafka
    participant InventoryService
    participant PaymentService

    Client->>OrderService: POST /orders
    activate OrderService
    OrderService->>OrderService: Create Order (PENDING)
    OrderService->>Kafka: Publish ORDER_CREATED
    OrderService-->>Client: 201 Created
    deactivate OrderService

    Kafka->>InventoryService: ORDER_CREATED Event
    activate InventoryService
    InventoryService->>InventoryService: Reserve Inventory
    InventoryService->>Kafka: Publish INVENTORY_RESERVED
    deactivate InventoryService

    Kafka->>PaymentService: ORDER_CREATED Event
    activate PaymentService
    PaymentService->>PaymentService: Process Payment (APPROVED)
    PaymentService->>Kafka: Publish PAYMENT_APPROVED
    deactivate PaymentService

    loop Polling
        Client->>OrderService: GET /orders/{orderId}
        OrderService-->>Client: Order Status (APPROVED)
    end

    Note over OrderService,PaymentService: Order completed successfully
```

### Failure Flow (실패 시나리오)

```mermaid
sequenceDiagram
    participant Client
    participant OrderService
    participant Kafka
    participant InventoryService
    participant PaymentService

    Client->>OrderService: POST /orders (userId=2)
    activate OrderService
    OrderService->>OrderService: Create Order (PENDING)
    OrderService->>Kafka: Publish ORDER_CREATED
    OrderService-->>Client: 201 Created
    deactivate OrderService

    Kafka->>InventoryService: ORDER_CREATED Event
    activate InventoryService
    InventoryService->>InventoryService: Reserve Inventory
    InventoryService->>Kafka: Publish INVENTORY_RESERVED
    deactivate InventoryService

    Kafka->>PaymentService: ORDER_CREATED Event
    activate PaymentService
    PaymentService->>PaymentService: Process Payment (FAILED)
    PaymentService->>Kafka: Publish PAYMENT_FAILED
    deactivate PaymentService

    Kafka->>InventoryService: PAYMENT_FAILED Event
    activate InventoryService
    InventoryService->>InventoryService: Rollback Inventory (Cancel)
    deactivate InventoryService

    Kafka->>OrderService: PAYMENT_FAILED Event
    activate OrderService
    OrderService->>OrderService: Update Order (CANCELED)
    deactivate OrderService

    loop Polling
        Client->>OrderService: GET /orders/{orderId}
        OrderService-->>Client: Order Status (CANCELED)
    end

    Note over OrderService,PaymentService: Order canceled with compensation
```

### Event Types

| Service | Published Events | Subscribed Events |
|---------|-----------------|-------------------|
| **Order Service** | `ORDER_CREATED` | `PAYMENT_FAILED`, `INVENTORY_FAILED` |
| **Inventory Service** | `INVENTORY_RESERVED`, `INVENTORY_FAILED` | `ORDER_CREATED`, `PAYMENT_FAILED` |
| **Payment Service** | `PAYMENT_APPROVED`, `PAYMENT_FAILED` | `ORDER_CREATED` |

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Kafka (Confluent Platform 7.5.0)
- Gradle
- Docker & Docker Compose
