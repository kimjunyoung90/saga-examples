package com.example.producer.event;

import lombok.Builder;

@Builder
public record InventoryCreated(
        Long inventoryId,
        Long productId,
        String status
) {
}
