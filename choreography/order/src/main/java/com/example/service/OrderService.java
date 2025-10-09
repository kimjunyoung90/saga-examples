package com.example.service;

import com.example.entity.Orders;
import com.example.event.OrderCreatedEvent;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Orders create(Orders order) {
        Orders orders = orderRepository.save(order);
        kafkaTemplate.send("order", new OrderCreatedEvent(orders.getId()));
        return orders;
    }

    public Orders findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Orders update(Long id, Orders order) {
        Orders existingOrder = findById(id);
        existingOrder.setProductId(order.getProductId());
        existingOrder.setQuantity(order.getQuantity());
        existingOrder.setPrice(order.getPrice());
        existingOrder.setTotalAmount(order.getTotalAmount());
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
