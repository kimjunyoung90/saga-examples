package com.order.producer.event;

import lombok.Builder;

@Builder
public record OrderMessage(
        String type,
        Object payload
) {
}
