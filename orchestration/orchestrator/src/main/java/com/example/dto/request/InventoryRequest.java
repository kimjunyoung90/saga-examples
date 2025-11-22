package com.example.dto.request;

public record InventoryRequest(Long orderId, Long productId, Integer quantity) {
}
