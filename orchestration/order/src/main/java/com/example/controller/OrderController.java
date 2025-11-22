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

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest orderRequest) {
        Order created = orderService.create(orderRequest);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        List<Order> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
//        Order updated = orderService.update(id, order);
//        return ResponseEntity.ok(updated);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        orderService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
