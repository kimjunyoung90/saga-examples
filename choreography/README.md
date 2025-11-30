# Choreography Saga Pattern

이 프로젝트는 **Choreography** 방식의 **Saga Pattern**을 구현한 예제입니다.

## Choreography 란?

Choreography 방식은 중앙 조정자 없이 각 서비스가 이벤트를 발행하고 구독하여 분산 트랜잭션을 관리합니다. Orchestration과 달리, 각 서비스는 독립적으로 이벤트에 반응하며 자율적으로 동작합니다.

### 주요 특징

- **분산 제어**: 중앙 조정자 없이 각 서비스가 독립적으로 동작
- **이벤트 기반**: 서비스 간 느슨한 결합으로 확장성 향상
- **자율성**: 각 서비스가 자신의 로직을 독립적으로 관리
- **복잡도 분산**: 비즈니스 로직이 여러 서비스에 분산
- **보상 트랜잭션**: 실패 시 이벤트 기반 자동 롤백

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

    Kafka->>OrderService: PAYMENT_APPROVED Event
    activate OrderService
    OrderService->>OrderService: Update Order (APPROVED)
    deactivate OrderService

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
| **Order Service** | `ORDER_CREATED` | `PAYMENT_APPROVED`, `PAYMENT_FAILED`, `INVENTORY_FAILED` |
| **Inventory Service** | `INVENTORY_RESERVED`, `INVENTORY_FAILED` | `ORDER_CREATED`, `PAYMENT_FAILED` |
| **Payment Service** | `PAYMENT_APPROVED`, `PAYMENT_FAILED` | `ORDER_CREATED` |

