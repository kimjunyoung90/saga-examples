package com.example.producer.event;

import lombok.Builder;

@Builder
public record InventoryCreatedEvent(
        Long inventoryId,
        Long productId,
        String status
) {
}
