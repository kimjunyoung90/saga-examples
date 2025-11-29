package com.example.service;

import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.exception.PaymentFailedException;
import com.example.exception.PaymentNotFoundException;
import com.example.producer.PaymentEventProducer;
import com.example.producer.event.MessageType;
import com.example.producer.event.PaymentCreated;
import com.example.producer.event.PaymentMessage;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public Payment create(PaymentRequest paymentRequest) {

        if (paymentRequest.userId() == 2) {
            throw new PaymentFailedException();
        }

        Payment payment = Payment.builder()
                .orderId(paymentRequest.orderId())
                .userId(paymentRequest.userId())
                .totalAmount(paymentRequest.totalAmount())
                .status(Payment.PaymentStatus.APPROVED)
                .build();

        payment = paymentRepository.save(payment);

        PaymentCreated payload = PaymentCreated.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .status(Payment.PaymentStatus.APPROVED.name())
                .build();

        PaymentMessage message = PaymentMessage.builder()
                .type(MessageType.PAYMENT_APPROVED.name())
                .payload(payload)
                .build();
        paymentEventProducer.publishPaymentCreated(message);
        return payment;
    }

    @Transactional
    public void cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException());

        payment.updateStatus(Payment.PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
