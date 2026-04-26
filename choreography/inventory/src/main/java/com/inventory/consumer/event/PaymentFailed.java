package com.inventory.consumer.event;

public record PaymentFailed(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
