package com.jylab.controller;

import com.jylab.entity.Orders;
import lombok.RequiredArgsConstructor;
import com.jylab.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Orders>> getOrders() {
        List<Orders> orders = orderService.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Orders> getOrder(@PathVariable Long orderId) {
        Orders order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequest orderDto) {
        Orders order = orderService.createOrder(orderDto);
        return ResponseEntity
                .created(URI.create("/orders/" + order.getId()))
                .body(order);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
