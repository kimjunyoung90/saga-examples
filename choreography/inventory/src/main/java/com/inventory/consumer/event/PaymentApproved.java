package com.inventory.consumer.event;

public record PaymentApproved(
        Long orderId
) {
}
