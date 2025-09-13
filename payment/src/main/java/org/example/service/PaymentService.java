package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.controller.PaymentRequeset;
import org.example.entity.Payment;
import org.example.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }

    public Payment createPayment(PaymentRequeset paymentRequeset) {
        Payment payment = new Payment();
        payment.setOrderId(paymentRequeset.getOrderId());
        payment.setAmount(paymentRequeset.getAmount());
        return paymentRepository.save(payment);
    }

    public void cancelPayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
