# 데이터베이스 명세서

## 1. Order Service

### 1.1 Orders (주문)

**테이블명**: `orders`

**설명**: 주문 정보를 저장하는 테이블 (단일 상품 주문만 가능)

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 주문 ID (자동 생성) |
| product_id | BIGINT | NOT NULL | 상품 ID |
| quantity | INTEGER | NOT NULL | 주문 수량 |
| price | BIGINT | NOT NULL | 주문 당시 상품 단가 |
| total_amount | BIGINT | NOT NULL | 주문 총액 (quantity × price) |

**인덱스**:
- PRIMARY KEY: `id`
- INDEX: `product_id` (상품별 주문 조회)

**비고**:
- 한 주문에 하나의 상품만 포함 가능
- `product_id`는 Inventory Service의 상품을 논리적으로 참조 (물리적 FK 없음)
- `price`는 주문 당시의 가격을 저장 (가격 변동 이력 유지)
- `total_amount`는 계산된 값이지만 조회 성능을 위해 저장

---

## 2. Inventory Service

### 2.1 Inventory (재고)

**테이블명**: `inventory`

**설명**: 상품 재고 정보를 저장하는 테이블

| 컬럼명 | 데이터 타입 | 제약 조건 | 설명 |
|--------|------------|----------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 재고 ID (자동 생성) |
| product_id | BIGINT | UNIQUE, NOT NULL | 상품 ID |
| quantity | INTEGER | NOT NULL | 재고 수량 |

**인덱스**:
- PRIMARY KEY: `id`
- UNIQUE INDEX: `product_id`

**비고**:
- `id`는 내부 관리용 기본키
- `product_id`는 외부 시스템에서 관리되는 상품 ID
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
┌─────────────────────────┐
│     Orders              │
├─────────────────────────┤
│ PK: id                  │
│     product_id          │
│     quantity            │
│     price               │
│     total_amount        │
└─────────────────────────┘

┌─────────────────────────┐
│    Inventory            │
├─────────────────────────┤
│ PK: id                  │
│ UK: product_id          │
│     quantity            │
└─────────────────────────┘

┌─────────────────────────┐
│     Payment             │
├─────────────────────────┤
│ PK: id                  │
│     order_id            │
│     amount              │
└─────────────────────────┘
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
