package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderResponse(
    @JsonProperty("id") Long orderId,
    Long productId,
    Integer quantity,
    Long price,
    Long totalAmount
) {
}
