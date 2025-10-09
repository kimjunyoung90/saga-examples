package com.example.event;

public record InventoryDecreasedEvent(
        Long inventoryId,
        Long productId,
        Integer quantity,
        Long orderId
) {
}
