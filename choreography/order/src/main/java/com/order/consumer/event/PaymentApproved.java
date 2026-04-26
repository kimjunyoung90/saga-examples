package com.order.consumer.event;

public record PaymentApproved(
        Long orderId,
        Long paymentId
) {
}
