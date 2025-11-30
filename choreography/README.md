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

1. **주문 생성**: Order Service가 주문 생성 이벤트 발행
2. **재고 확인**: Inventory Service가 이벤트를 구독하고 재고 차감
3. **결제 처리**: Payment Service가 이벤트를 구독하고 결제 진행
4. **보상 트랜잭션**: 실패 시 각 서비스가 보상 이벤트 발행/처리

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Kafka (Confluent Platform 7.5.0)
- Gradle
- Docker & Docker Compose
