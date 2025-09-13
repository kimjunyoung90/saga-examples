package com.jylab.controller;

import lombok.RequiredArgsConstructor;
import com.jylab.entity.Order;
import com.jylab.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping
    public void createOrder(@RequestBody OrderRequest orderDto) {
        Order order = orderService.createOrder(orderDto);
    }

    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
