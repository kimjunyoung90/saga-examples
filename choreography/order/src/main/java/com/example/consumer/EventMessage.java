package com.example.consumer;

import com.fasterxml.jackson.databind.JsonNode;

public record EventMessage(
        EventType type,
        JsonNode payload
) {
}
