package org.example.controller;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long totalAmount;
    private List<OrderItemRequest> orderItemRequests;
}
