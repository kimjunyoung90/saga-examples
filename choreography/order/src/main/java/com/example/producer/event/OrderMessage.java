package com.example.producer.event;

import lombok.Builder;

@Builder
public record OrderMessage(
        String type,
        Object payload
) {
}
