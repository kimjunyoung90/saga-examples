package com.order.consumer.event;

public record InventoryEventFailed(
        Long orderId
) {
}
