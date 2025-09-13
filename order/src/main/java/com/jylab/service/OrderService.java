package com.jylab.service;

import lombok.RequiredArgsConstructor;
import com.jylab.controller.OrderRequest;
import com.jylab.entity.Order;
import com.jylab.entity.OrderItem;
import com.jylab.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setTotalAmount(orderRequest.getTotalAmount());

        List<OrderItem> orderItems = orderRequest.getOrderItemRequests().stream()
                        .map(orderItemRequest -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setProductId(orderItemRequest.getProductId());
                            orderItem.setQuantity(orderItemRequest.getQuantity());
                            orderItem.setOrder(order);
                            return orderItem;
                        }).toList();

        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}