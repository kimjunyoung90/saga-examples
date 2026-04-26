package com.example.service;

import com.example.constant.KafkaTopics;
import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.outbox.OutboxService;
import com.example.producer.event.MessageType;
import com.example.producer.event.PaymentCreated;
import com.example.producer.event.PaymentFailed;
import com.example.producer.event.PaymentMessage;
import com.example.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.entity.Payment.PaymentStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;

    //결제
    @Transactional
    public Payment create(PaymentRequest paymentRequest) {

        Payment newPayment = Payment.builder()
                .userId(paymentRequest.userId())
                .orderId(paymentRequest.orderId())
                .totalAmount(paymentRequest.amount())
                .status(PENDING)
                .build();
        Payment payment = paymentRepository.save(newPayment);

        payment.updateStatus(paymentRequest.userId() == 2 ? FAILED : APPROVED);
        payment = paymentRepository.save(payment);

        if (payment.getStatus() == FAILED) {
            recordFailedEvent(payment);
        } else {
            recordApprovedEvent(payment);
        }

        return payment;
    }

    private void recordApprovedEvent(Payment payment) {
        PaymentCreated payload = PaymentCreated.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .status(APPROVED.name())
                .build();

        //outbox pattern
        PaymentMessage envelope = PaymentMessage.builder()
                .type(MessageType.PAYMENT_APPROVED.name())
                .payload(payload)
                .build();

        outboxService.record(
                KafkaTopics.PAYMENT_EVENTS,
                MessageType.PAYMENT_APPROVED.name(),
                String.valueOf(payment.getOrderId()),
                envelope
        );
    }

    private void recordFailedEvent(Payment payment) {
        PaymentFailed payload = PaymentFailed.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .status(FAILED.name())
                .build();

        PaymentMessage envelope = PaymentMessage.builder()
                .type(MessageType.PAYMENT_FAILED.name())
                .payload(payload)
                .build();

        outboxService.record(
                KafkaTopics.PAYMENT_EVENTS,
                MessageType.PAYMENT_FAILED.name(),
                String.valueOf(payment.getOrderId()),
                envelope
        );
    }
}
