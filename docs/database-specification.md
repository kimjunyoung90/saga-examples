# 데이터베이스 명세서

## 1. Order Service

### 1.1 Orders (주문)

**테이블명**: `orders`

**설명**: 주문 정보를 저장하는 테이블

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 주문 ID (자동 생성) |
| total_amount | BIGINT | NOT NULL | 주문 총액 |

**인덱스**:
- PRIMARY KEY: `id`

**관계**:
- `order_item` 테이블과 1:N 관계 (부모)

---

### 1.2 Order_Item (주문 항목)

**테이블명**: `order_item`

**설명**: 주문에 포함된 개별 상품 정보

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 주문 항목 ID (자동 생성) |
| order_id | BIGINT | NOT NULL, FOREIGN KEY | 주문 ID (Orders.id 참조) |
| product_id | BIGINT | NOT NULL | 상품 ID |
| quantity | BIGINT | NOT NULL | 주문 수량 |

**인덱스**:
- PRIMARY KEY: `id`
- FOREIGN KEY: `order_id` → `orders(id)`

**관계**:
- `orders` 테이블과 N:1 관계 (자식)

**Cascade 설정**:
- CascadeType.ALL: 주문 삭제 시 모든 주문 항목도 삭제
- orphanRemoval = true: 주문에서 제거된 항목은 자동 삭제

---

## 2. Inventory Service

### 2.1 Inventory (재고)

**테이블명**: `inventory`

**설명**: 상품 재고 정보를 저장하는 테이블

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| product_id | BIGINT | PRIMARY KEY | 상품 ID |
| quantity | INTEGER | NOT NULL | 재고 수량 |

**인덱스**:
- PRIMARY KEY: `product_id`

**비고**:
- `product_id`가 기본키로, 외부 시스템에서 관리되는 상품 ID를 사용
- 재고 차감/복구 시 동시성 제어 필요

---

## 3. Payment Service

### 3.1 Payment (결제)

**테이블명**: `payment`

**설명**: 결제 정보를 저장하는 테이블

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 결제 ID (자동 생성) |
| order_id | BIGINT | NOT NULL | 주문 ID (논리적 참조) |
| amount | BIGINT | NOT NULL | 결제 금액 |

**인덱스**:
- PRIMARY KEY: `id`
- INDEX: `order_id` (조회 성능 향상)

**비고**:
- `order_id`는 Order Service의 주문 ID를 논리적으로 참조
- 마이크로서비스 간 직접적인 FK 제약 없음 (서비스 독립성 유지)

---

## 4. ERD (Entity Relationship Diagram)

```
┌─────────────────────┐
│     Orders          │
├─────────────────────┤
│ PK: id              │
│     total_amount    │
└─────────────────────┘
          │ 1
          │
          │ N
┌─────────────────────┐
│    Order_Item       │
├─────────────────────┤
│ PK: id              │
│ FK: order_id        │
│     product_id      │
│     quantity        │
└─────────────────────┘

┌─────────────────────┐
│    Inventory        │
├─────────────────────┤
│ PK: product_id      │
│     quantity        │
└─────────────────────┘

┌─────────────────────┐
│     Payment         │
├─────────────────────┤
│ PK: id              │
│     order_id        │
│     amount          │
└─────────────────────┘
```

---

## 5. 데이터베이스 설정

### 5.1 공통 설정
- **Database Type**: H2 (In-Memory)
- **Hibernate DDL**: create-drop (개발 환경)
- **Encoding**: UTF-8

### 5.2 서비스별 데이터베이스

| 서비스 | 데이터베이스명 | URL |
|--------|---------------|-----|
| Order Service | orderdb | jdbc:h2:mem:orderdb |
| Inventory Service | inventorydb | jdbc:h2:mem:inventorydb |
| Payment Service | paymentdb | jdbc:h2:mem:paymentdb |

---

## 6. 초기 데이터

### 6.1 Inventory (재고 초기 데이터)

```sql
INSERT INTO inventory (product_id, quantity) VALUES (1, 100);
INSERT INTO inventory (product_id, quantity) VALUES (2, 50);
INSERT INTO inventory (product_id, quantity) VALUES (3, 200);
```

**위치**: `/orchestration/inventory/src/main/resources/data.sql`
