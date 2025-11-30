package com.example.dto;

public record InventoryCancelRequest(
    Long productId,
    Integer quantity
) {
}
