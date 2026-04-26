package com.order.producer.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreated(
    Long orderId,
    Long userId,
    Long productId,
    Integer quantity,
    BigDecimal amount
){}
