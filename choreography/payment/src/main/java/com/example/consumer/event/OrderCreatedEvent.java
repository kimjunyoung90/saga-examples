package com.example.consumer.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal amount
) {
}
