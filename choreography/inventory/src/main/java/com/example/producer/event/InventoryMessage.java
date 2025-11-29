package com.example.producer.event;

import lombok.Builder;

@Builder
public record InventoryMessage(
     String type,
     Object payload
) {
}
