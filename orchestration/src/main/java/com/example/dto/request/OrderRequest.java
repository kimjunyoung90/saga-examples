package com.example.dto.request;

import java.util.List;

public record OrderRequest(Long totalAmount, List<OrderItemRequest> orderItemRequest) {
}
