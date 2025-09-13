package com.jylab.service;

import lombok.RequiredArgsConstructor;
import com.jylab.controller.PaymentRequest;
import com.jylab.entity.Payment;
import com.jylab.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow();
    }

    public Payment createPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setAmount(paymentRequest.getAmount());
        return paymentRepository.save(payment);
    }

    public void cancelPayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
