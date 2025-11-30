package com.example.service;

import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.exception.PaymentFailedException;
import com.example.exception.PaymentNotFoundException;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment create(PaymentRequest paymentRequest) {

        //1. 결제 요청
        Payment payment = Payment.builder()
                .orderId(paymentRequest.orderId())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        //2. 결제
        payment.updateStatus(paymentRequest.userId() == 2 ? Payment.PaymentStatus.FAILED : Payment.PaymentStatus.APPROVED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException());

        payment.updateStatus(Payment.PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
