package com.example.service;

import com.example.dto.OrderRequest;
import com.example.entity.Order;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order create(OrderRequest orderRequest) {
        Order order = Order.builder()
                .userId(orderRequest.userId())
                .productId(orderRequest.productId())
                .quantity(orderRequest.quantity())
                .price(orderRequest.price())
                .build();
        return orderRepository.save(order);
    }

//    @Transactional
//    public Order update(Long id, Order order) {
//        Order existingOrder = findById(id);
//        existingOrder.setProductId(order.getProductId());
//        existingOrder.setQuantity(order.getQuantity());
//        existingOrder.setPrice(order.getPrice());
//        existingOrder.setTotalAmount(order.getTotalAmount());
//        return orderRepository.save(existingOrder);
//    }
//
//    @Transactional
//    public void delete(Long id) {
//        orderRepository.deleteById(id);
//    }
}
