package com.example.dto.request;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        Long userId,
        BigDecimal totalAmount
) {
}
