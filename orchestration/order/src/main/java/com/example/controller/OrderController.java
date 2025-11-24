package com.example.controller;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 요청
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest orderRequest) {
        Order created = orderService.create(orderRequest);
        return ResponseEntity.ok(created);
    }

    // 주문 취소
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Order> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
