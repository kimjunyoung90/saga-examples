package com.payment.service;

import com.payment.constant.KafkaTopics;
import com.payment.dto.PaymentRequest;
import com.payment.entity.Payment;
import com.payment.outbox.OutboxService;
import com.payment.producer.event.MessageType;
import com.payment.producer.event.PaymentCreated;
import com.payment.producer.event.PaymentFailed;
import com.payment.producer.event.PaymentMessage;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.payment.entity.Payment.PaymentStatus.*;

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
