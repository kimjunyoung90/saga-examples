package com.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long userId,
        Long orderId,
        BigDecimal amount
) {
}
