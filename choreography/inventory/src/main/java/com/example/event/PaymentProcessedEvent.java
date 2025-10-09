package com.example.event;

public record PaymentProcessedEvent(
        Long paymentId,
        Long orderId,
        Long productId,
        Integer quantity,
        Long amount
) {
}
