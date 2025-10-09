package com.example.service;

import com.example.entity.Orders;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Orders create(Orders order) {
        return orderRepository.save(order);
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
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setOrderItems(order.getOrderItems());
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
