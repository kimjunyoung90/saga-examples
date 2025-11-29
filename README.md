# Saga Pattern Examples

MSA 분산 트랜잭션 Saga 패턴 구현 예제  

고객의 상품 구매 프로세스를 Orchestration 방식, Choreography 방식으로 구현하여 차이 점을 확인하는 프로젝트 입니다.

> Saga 패턴이란?  
> 분산 환경에서 여러 시스템에 걸쳐 연계된 트랜잭션 수행이 필요할 때, 각 시스템에서 로컬 트랜잭션을 수행하고, 중간 단계에서 실패가 발생할 경우 보상 트랜잭션을 실행하여 데이터의 일관성을 맞추는 방법

## 서비스

```
├── order/           # 주문 서비스 (Port: 8081)
├── inventory/       # 재고 서비스 (Port: 8082)
├── payment/         # 결제 서비스 (Port: 8083)
├── orchestration/   # Saga Orchestrator
└── choreography/    # Saga Choreography
```

## 시나리오
1. 고객이 상품 구매를 요청한다.
2. 주문 서비스에서는 구매 주문을 기록 한다.
3. 재고 서비스에서 물품 재고를 확보한다.
4. 결제 서비스에서 결제 처리를 진행한다.

```mermaid
sequenceDiagram
    participant Client
    participant OS as 🛍️ 주문 서비스
    participant IS as 📦 재고 서비스
    participant PS as 💳 결제 서비스

    title 상품 구매 프로세스

    %% --- 성공 흐름 (왼쪽) ---
        Client->> OS: 구매 요청
        activate Client
    
        activate OS
        OS->>OS: 1. 주문 생성
        OS->>IS: 2. 재고 확보
        deactivate OS

        activate IS
        IS->>IS: 2. 재고 차감/예약
        IS->>PS: 3. 결제 요청
        deactivate IS
        
        activate PS
        PS->>PS: 3. 결제 처리
        PS->>Client: 4. 응답
        deactivate PS
        
        
        deactivate Client
```

## 주요 포인트

1. **마이크로서비스 간 분산 트랜잭션 이해**
2. **Orchestration**, **Choreography** 방식의 차이 이해
3. **보상 트랜잭션** 개념 이해

## 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle
- **Database**: H2 (In-Memory)
- **ORM**: JPA/Hibernate
- **Message Broker**: Kafka
---
