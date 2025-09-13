package org.example.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequeset {
    private Long orderId;
    private Long amount;
}
