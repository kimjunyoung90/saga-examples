package com.example.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private Long productId;
    private Long quantity;
}
