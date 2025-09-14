package com.jylab.controller;

import com.jylab.entity.Orders;
import lombok.RequiredArgsConstructor;
import com.jylab.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Orders> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{orderId}")
    public Orders getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PostMapping
    public Orders createOrder(@RequestBody OrderRequest orderDto) {
        return orderService.createOrder(orderDto);
    }

    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
