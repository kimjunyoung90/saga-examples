package com.example.service;

import com.example.entity.Orders;
import lombok.RequiredArgsConstructor;
import com.example.controller.OrderRequest;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Orders createOrder(OrderRequest orderRequest) {
        Orders orders = new Orders();
        orders.setProductId(orderRequest.getProductId());
        orders.setQuantity(orderRequest.getQuantity());
        orders.setPrice(orderRequest.getPrice());
        orders.setTotalAmount(orderRequest.getQuantity() * orderRequest.getPrice());

        return orderRepository.save(orders);
    }

    public List<Orders> getOrders() {
        return orderRepository.findAll();
    }

    public Orders getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
