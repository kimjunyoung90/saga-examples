package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.exception.OrderNotFoundException;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.entity.Order.OrderStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order create(OrderRequest orderRequest) {
        Order order = Order.builder()
                .userId(orderRequest.userId())
                .productId(orderRequest.productId())
                .quantity(orderRequest.quantity())
                .price(orderRequest.price())
                .status(PENDING)
                .build();
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());

        order.updateStatus(Order.OrderStatus.CANCELED);
        return orderRepository.save(order);
    }
}
