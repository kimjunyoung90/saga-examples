package com.example.producer.event;

import lombok.Builder;

@Builder
public record InventoryEvent(
     String type,
     Object payload
) {
}
