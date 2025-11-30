package com.example.consumer;

public record PaymentFailed(
        Long orderId,
        Long paymentId
) {
}
