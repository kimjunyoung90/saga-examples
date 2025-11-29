package com.example.dto;

public record CancelInventoryRequest(
    Long productId,
    int quantity
) {
}
