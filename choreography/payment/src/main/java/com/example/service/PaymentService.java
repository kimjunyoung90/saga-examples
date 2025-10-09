package com.example.service;

import com.example.entity.Payment;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    public Optional<Payment> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment update(Long id, Payment payment) {
        Payment existingPayment = findById(id);
        existingPayment.setOrderId(payment.getOrderId());
        existingPayment.setAmount(payment.getAmount());
        return paymentRepository.save(existingPayment);
    }

    @Transactional
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    @Transactional
    public void cancelPayment(Long orderId) {
        Payment payment = findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        paymentRepository.delete(payment);
    }
}
