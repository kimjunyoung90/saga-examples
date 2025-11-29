package com.example.producer.event;

import lombok.Builder;

@Builder
public record PaymentEvent(
        String type,
        Object payload
) {
}
