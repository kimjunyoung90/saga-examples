package com.example.consumer.event;

public record PaymentApproved(
        Long orderId,
        Long paymentId
) {
}
