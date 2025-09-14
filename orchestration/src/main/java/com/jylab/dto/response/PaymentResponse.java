package com.jylab.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentResponse(@JsonProperty("id") Long paymentId, Long orderId, Long amount) {
}
