package com.example.consumer.event;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity
) {
}
