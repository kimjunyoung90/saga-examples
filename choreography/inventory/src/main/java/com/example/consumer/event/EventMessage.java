package com.example.consumer.event;

import com.fasterxml.jackson.databind.JsonNode;

public record EventMessage(
        EventType type,
        JsonNode payload
) {
}
