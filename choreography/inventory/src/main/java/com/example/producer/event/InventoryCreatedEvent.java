package com.example.producer.event;

import lombok.Builder;

@Builder
public record InventoryCreatedEvent(
        String eventType,
        Long inventoryId,
        Long productId,
        String status
) {
}
