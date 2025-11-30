package com.example.dto;

import lombok.Builder;

@Builder
public record InventoryRequest(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
