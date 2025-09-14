package com.jylab.dto.request;

public record InventoryRequest(Long orderId, Long productId, int quantity) {
}
