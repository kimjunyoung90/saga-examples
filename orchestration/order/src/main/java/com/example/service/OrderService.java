package com.example.service;

import com.example.entity.Orders;
import lombok.RequiredArgsConstructor;
import com.example.controller.OrderRequest;
import com.example.entity.OrderItem;
import com.example.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Orders createOrder(OrderRequest orderRequest) {
        Orders orders = new Orders();
        orders.setTotalAmount(orderRequest.getTotalAmount());

        List<OrderItem> orderItems = orderRequest.getOrderItemRequest().stream()
                        .map(orderItemRequest -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setProductId(orderItemRequest.getProductId());
                            orderItem.setQuantity(orderItemRequest.getQuantity());
                            orderItem.setOrders(orders);
                            return orderItem;
                        }).toList();

        orders.setOrderItems(orderItems);
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