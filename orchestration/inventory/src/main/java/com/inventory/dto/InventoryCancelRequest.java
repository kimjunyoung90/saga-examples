package com.inventory.dto;

public record InventoryCancelRequest(
    Long productId,
    Integer quantity
) {
}
