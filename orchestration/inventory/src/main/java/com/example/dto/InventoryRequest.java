package com.example.dto;

public record InventoryRequest(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
