package com.example.dto;

import lombok.Builder;

@Builder
public record InventoryCancelRequest(
        Long inventoryId,
        Long productId,
        Integer quantity,
        Long orderId
) {
}
