package com.example.dto;

import java.math.BigDecimal;

public record OrderRequest(
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal price
) {
}
