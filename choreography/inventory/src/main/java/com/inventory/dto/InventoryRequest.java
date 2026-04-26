package com.inventory.dto;

import lombok.Builder;

@Builder
public record InventoryRequest(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
