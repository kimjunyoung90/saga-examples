package com.order.consumer.event;

public record PaymentFailed(
        Long orderId,
        Long paymentId
) {
}
