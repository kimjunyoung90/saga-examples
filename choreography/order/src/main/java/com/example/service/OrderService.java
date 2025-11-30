package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.exception.OrderNotFoundException;
import com.example.producer.OrderEventProducer;
import com.example.producer.event.MessageType;
import com.example.producer.event.OrderCreated;
import com.example.producer.event.OrderMessage;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public Order create(OrderRequest orderRequest) {
        Order order = Order.builder()
                .userId(orderRequest.userId())
                .productId(orderRequest.productId())
                .quantity(orderRequest.quantity())
                .amount(orderRequest.amount())
                .build();
        order = orderRepository.save(order);

        //message
        OrderCreated payload = OrderCreated.builder()
                .orderId(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .amount(order.getAmount())
                .build();

        OrderMessage message = OrderMessage.builder()
                .type(MessageType.ORDER_CREATED.name())
                .payload(payload)
                .build();

        orderEventProducer.publishOrderCreated(message);

        return order;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());

        order.updateStatus(Order.OrderStatus.CANCELED);
        return orderRepository.save(order);
    }
}
