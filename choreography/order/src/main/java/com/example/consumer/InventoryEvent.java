package com.example.consumer;

public record InventoryEvent (
        String eventType,
        String payload
) {
}
