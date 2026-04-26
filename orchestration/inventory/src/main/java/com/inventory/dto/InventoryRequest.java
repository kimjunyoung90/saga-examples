package com.inventory.dto;

public record InventoryRequest(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
