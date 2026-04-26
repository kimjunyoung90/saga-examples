package com.example.service;

import com.example.constant.KafkaTopics;
import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.exception.OrderNotFoundException;
import com.example.outbox.OutboxService;
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
    private final OutboxService outboxService;

    //주문 발행
    @Transactional
    public Order create(OrderRequest orderRequest) {
        Order order = Order.builder()
                .userId(orderRequest.userId())
                .productId(orderRequest.productId())
                .quantity(orderRequest.quantity())
                .amount(orderRequest.amount())
                .build();
        order = orderRepository.save(order);

        OrderCreated payload = OrderCreated.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .amount(order.getAmount())
                .build();

        OrderMessage envelope = OrderMessage.builder()
                .type(MessageType.ORDER_CREATED.name())
                .payload(payload)
                .build();

        //outbox pattern 메시지 발행 보장
        outboxService.record(
                KafkaTopics.ORDER_EVENTS,
                MessageType.ORDER_CREATED.name(),
                String.valueOf(order.getId()),
                envelope
        );

        return order;
    }

    @Transactional
    public Order approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.updateStatus(Order.OrderStatus.APPROVED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.updateStatus(Order.OrderStatus.CANCELED);
        return orderRepository.save(order);
    }
}
