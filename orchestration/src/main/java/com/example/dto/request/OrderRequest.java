package com.example.dto.request;

public record OrderRequest(Long productId, Integer quantity, Long price) {
}
