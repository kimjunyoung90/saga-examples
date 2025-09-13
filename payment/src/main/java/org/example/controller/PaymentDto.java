package org.example.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto {
    private Long orderId;
    private Long amount;
}
