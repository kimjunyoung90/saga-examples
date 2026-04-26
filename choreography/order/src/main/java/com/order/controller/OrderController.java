package com.order.controller;

import com.order.dto.OrderRequest;
import com.order.entity.Order;
import com.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 요청
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.create(orderRequest);
        return ResponseEntity.created(URI.create("/orders/" + order.getId())).body(order);
    }

}
