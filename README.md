# Saga Pattern Examples

Spring Boot를 활용한 분산 트랜잭션 Saga 패턴 구현 예제

## 🏗️ 아키텍처

```
saga-examples/
├── order/           # 주문 서비스 (Port: 8081)
├── payment/         # 결제 서비스 (Port: 8082)
├── inventory/       # 재고 서비스 (Port: 8083)
├── orchestration/   # Saga Orchestrator (Port: 8080)
└── choreography/    # Saga Choreography (미구현)
```

## 🚀 실행 방법

### 1. 각 서비스 개별 실행
```bash
# 주문 서비스
cd order && ./gradlew bootRun

# 결제 서비스
cd payment && ./gradlew bootRun

# 재고 서비스
cd inventory && ./gradlew bootRun

# 오케스트레이션 서비스
cd orchestration && ./gradlew bootRun
```

### 2. Saga 주문 처리 테스트
```bash
curl -X POST http://localhost:8080/saga/order \
  -H "Content-Type: application/json" \
  -d '{
    "totalAmount": 30000,
    "orderItemRequest": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

## 📋 API 명세

### 주문 서비스 (8081)
- `POST /orders` - 주문 생성
- `GET /orders` - 주문 목록 조회
- `GET /orders/{id}` - 주문 상세 조회
- `DELETE /orders/{id}` - 주문 취소

### 결제 서비스 (8082)
- `POST /payment` - 결제 처리
- `GET /payment/{id}` - 결제 조회
- `DELETE /payment/{id}` - 결제 취소

### 재고 서비스 (8083)
- `GET /inventory/{productId}` - 재고 조회
- `PUT /inventory/{productId}/increase` - 재고 증가
- `PUT /inventory/{productId}/decrease` - 재고 감소
- `PUT /inventory/decrease` - 배치 재고 감소

## 🔄 Saga 패턴 흐름

### 정상 처리 흐름
```
1. 주문 생성 → 2. 결제 처리 → 3. 재고 차감 → ✅ 성공
```

### 보상 트랜잭션 흐름
```
주문 실패: ❌ 즉시 종료

결제 실패: 주문 생성 ✅ → 결제 실패 ❌ → 주문 취소 🔄

재고 실패: 주문 생성 ✅ → 결제 처리 ✅ → 재고 실패 ❌ → 결제 취소 🔄 → 주문 취소 🔄
```

## 🛠️ 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle
- **Database**: H2 (In-Memory)
- **Communication**: WebClient (Reactive)

## 📝 주요 특징

- ✅ **분산 트랜잭션 관리**: Saga 패턴을 통한 최종 일관성 보장
- ✅ **보상 트랜잭션**: 실패 시 자동 롤백 처리
- ✅ **마이크로서비스 아키텍처**: 서비스별 독립 배포 가능

## 📚 학습 포인트

1. **Saga Orchestration Pattern** 이해
2. **보상 트랜잭션(Compensating Transaction)** 구현
3. **마이크로서비스 간 통신** 패턴
4. **분산 시스템에서의 데이터 일관성** 관리

---
*Made with ❤️ for learning distributed transaction patterns*