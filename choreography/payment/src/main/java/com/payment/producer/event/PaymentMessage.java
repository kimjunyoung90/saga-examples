package com.payment.producer.event;

import lombok.Builder;

@Builder
public record PaymentMessage(
        String type,
        Object payload
) {
}
