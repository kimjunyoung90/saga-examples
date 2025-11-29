package com.example.service;

import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.exception.PaymentNotFoundException;
import com.example.producer.PaymentEventProducer;
import com.example.producer.event.EventType;
import com.example.producer.event.PaymentCreatedEvent;
import com.example.producer.event.PaymentEvent;
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
        Payment payment = Payment.builder()
                .orderId(paymentRequest.orderId())
                .userId(paymentRequest.userId())
                .totalAmount(paymentRequest.totalAmount())
                .build();
        payment = paymentRepository.save(payment);

        PaymentCreatedEvent payload = PaymentCreatedEvent.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .status("PAYMENT_APPROVED")
                .build();

        PaymentEvent event = PaymentEvent.builder()
                .type(EventType.PAYMENT_APPROVED.name())
                .payload(payload)
                .build();
        paymentEventProducer.publishPaymentCreated(event);
        return payment;
    }

    @Transactional
    public void cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(""));

        payment.updateStatus(Payment.PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
