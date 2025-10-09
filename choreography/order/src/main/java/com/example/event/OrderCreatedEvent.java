package com.example.event;

public record OrderCreatedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        Long price,
        Long totalAmount
) {
}
