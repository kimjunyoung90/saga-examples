package com.example.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        Long userId,
        Long orderId,
        BigDecimal amount
) {
}
