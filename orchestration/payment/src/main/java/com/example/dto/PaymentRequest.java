package com.example.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        Long userId,
        BigDecimal totalAmount
) {
}
