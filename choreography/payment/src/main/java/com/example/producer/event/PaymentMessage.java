package com.example.producer.event;

import lombok.Builder;

@Builder
public record PaymentMessage(
        String type,
        Object payload
) {
}
