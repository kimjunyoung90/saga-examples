package com.example.producer.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentCreated(
        Long paymentId,
        Long orderId,
        Long userId,
        BigDecimal totalAmount,
        String status
) {
}
