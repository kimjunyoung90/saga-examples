package com.inventory.producer.event;

import lombok.Builder;

@Builder
public record InventoryConfirmed(
        Long orderId,
        Long productId,
        Integer quantity
) {
}
