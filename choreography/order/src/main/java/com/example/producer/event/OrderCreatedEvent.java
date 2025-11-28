package com.example.producer.event;

import lombok.Builder;

@Builder
public record OrderCreatedEvent(
    Long orderId,
    Long userId,
    Long productId,
    Integer quantity,
    String status
){}
