# 분산 트랜잭션 패턴: Orchestration vs. Choreography

이 문서는 마이크로서비스 기반 시스템에서 분산 트랜잭션의 흐름을 설명하고, 두 가지 일반적인 패턴인 Orchestration과 Choreography를 보여줍니다.

## 시나리오: 전자상거래 주문

고객이 주문하는 일반적인 전자상거래 시나리오를 사용합니다. 여기에는 세 가지 서비스가 포함됩니다.
*   **주문 서비스:** 주문을 생성합니다.
*   **결제 서비스:** 결제를 처리합니다.
*   **재고 서비스:** 재고를 예약합니다.

## 일반적인 흐름

```mermaid
graph TD
    A[주문] --> B[결제];
    B --> C[재고 처리];
```

## 1. Orchestration (오케스트레이션)

오케스트레이션 패턴에서는 중앙 "오케스트레이터" 서비스(이 경우 **주문 서비스**)가 전체 트랜잭션을 관리하고 각 서비스에 수행할 작업을 지시합니다.

```mermaid
sequenceDiagram
    participant 고객
    participant 주문 서비스 (오케스트레이터)
    participant 결제 서비스
    participant 재고 서비스

    고객->>+주문 서비스 (오케스트레이터): POST /orders (주문 요청)
    주문 서비스 (오케스트레이터)->>+결제 서비스: POST /payments (결제 요청)
    결제 서비스-->>-주문 서비스 (오케스트레이터): 200 OK (결제 성공)
    주문 서비스 (오케스트레이터)->>+재고 서비스: POST /inventory/reserve (재고 예약)
    재고 서비스-->>-주문 서비스 (오케스트레이터): 200 OK (재고 예약 성공)
    주문 서비스 (오케스트레이터)-->>-고객: 201 Created (주문 생성 완료)

    alt 결제 실패
        고객->>+주문 서비스 (오케스트레이터): POST /orders (주문 요청)
        주문 서비스 (오케스트레이터)->>+결제 서비스: POST /payments (결제 요청)
        결제 서비스-->>-주문 서비스 (오케스트레이터): 400 Bad Request (결제 실패)
        주문 서비스 (오케스트레이터)-->>-고객: 500 Internal Server Error (서버 오류)
    end

    alt 재고 예약 실패
        고객->>+주문 서비스 (오케스트레이터): POST /orders (주문 요청)
        주문 서비스 (오케스트레이터)->>+결제 서비스: POST /payments (결제 요청)
        결제 서비스-->>-주문 서비스 (오케스트레이터): 200 OK (결제 성공)
        주문 서비스 (오케스트레이터)->>+재고 서비스: POST /inventory/reserve (재고 예약)
        재고 서비스-->>-주문 서비스 (오케스트레이터): 400 Bad Request (재고 예약 실패)
        주문 서비스 (오케스트레이터)->>+결제 서비스: DELETE /payments/{paymentId} (보상 트랜잭션: 결제 취소)
        결제 서비스-->>-주문 서비스 (오케스트레이터): 200 OK (결제 취소 성공)
        주문 서비스 (오케스트레이터)-->>-고객: 500 Internal Server Error (서버 오류)
    end
```

## 2. Choreography (코레오그래피)

코레오그래피 패턴에서 각 서비스는 독립적으로 작동하고 다른 서비스에서 게시한 이벤트에 반응합니다. 중앙 조정자는 없습니다.

```mermaid
sequenceDiagram
    participant 고객
    participant 주문 서비스
    participant 메시지 브로커
    participant 결제 서비스
    participant 재고 서비스

    고객->>+주문 서비스: POST /orders (주문 요청)
    주문 서비스->>-메시지 브로커: [OrderCreated Event] 발행
    메시지 브로커->>+결제 서비스: [OrderCreated Event] 구독
    결제 서비스->>-메시지 브로커: [PaymentProcessed Event] 발행
    메시지 브로커->>+재고 서비스: [PaymentProcessed Event] 구독
    재고 서비스->>-메시지 브로커: [InventoryReserved Event] 발행
    메시지 브로커->>+주문 서비스: [InventoryReserved Event] 구독
    주문 서비스-->>-고객: 201 Created (주문 생성 완료)

    alt 결제 실패
        고객->>+주문 서비스: POST /orders (주문 요청)
        주문 서비스->>-메시지 브로커: [OrderCreated Event] 발행
        메시지 브로커->>+결제 서비스: [OrderCreated Event] 구독
        결제 서비스->>-메시지 브로커: [PaymentFailed Event] 발행
        메시지 브로커->>+주문 서비스: [PaymentFailed Event] 구독
        주문 서비스-->>-고객: 500 Internal Server Error (서버 오류)
    end

    alt 재고 예약 실패
        고객->>+주문 서비스: POST /orders (주문 요청)
        주문 서비스->>-메시지 브로커: [OrderCreated Event] 발행
        메시지 브로커->>+결제 서비스: [OrderCreated Event] 구독
        결제 서비스->>-메시지 브로커: [PaymentProcessed Event] 발행
        메시지 브로커->>+재고 서비스: [PaymentProcessed Event] 구독
        재고 서비스->>-메시지 브로커: [InventoryReservationFailed Event] 발행
        메시지 브로커->>+주문 서비스: [InventoryReservationFailed Event] 구독
        메시지 브로커->>+결제 서비스: [InventoryReservationFailed Event] 구독 (보상 트랜잭션)
        결제 서비스->>-메시지 브로커: [PaymentRefunded Event] 발행
        주문 서비스-->>-고객: 500 Internal Server Error (서버 오류)
    end
```
