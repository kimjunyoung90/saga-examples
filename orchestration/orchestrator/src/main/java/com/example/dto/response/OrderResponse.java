package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderResponse(
    @JsonProperty("id") Long orderId,
    Long userId,
    Long productId,
    Integer quantity,
    BigDecimal price
) {
}
