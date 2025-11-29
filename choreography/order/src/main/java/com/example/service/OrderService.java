package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.exception.OrderNotFoundException;
import com.example.producer.OrderEventProducer;
import com.example.producer.event.EventType;
import com.example.producer.event.OrderCreatedEvent;
import com.example.producer.event.OrderEvent;
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
                .price(orderRequest.price())
                .build();
        order = orderRepository.save(order);

        //event
        OrderCreatedEvent payload = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status("ORDER_CREATED")
                .build();

        OrderEvent event = OrderEvent.builder()
                .type(EventType.ORDER_CREATED.name())
                .payload(payload)
                .build();

        orderEventProducer.publishOrderCreated(event);

        return order;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(""));

        order.updateStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(order);
    }
}
