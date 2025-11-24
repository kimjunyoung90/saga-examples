package com.example.dto.request;

public record OrderRequest(Long userId, Long productId, Integer quantity, Long price) {
}
