package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderResponse(@JsonProperty("id") Long orderId, Long totalAmount, List<OrderItem> orderItems) {
}
