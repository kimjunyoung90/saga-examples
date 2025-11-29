package com.example.producer.event;

import lombok.Builder;

@Builder
public record OrderEvent(
        String type,
        Object payload
) {
}
