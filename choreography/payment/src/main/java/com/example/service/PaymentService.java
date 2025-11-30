package com.example.service;

import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.exception.PaymentNotFoundException;
import com.example.producer.PaymentEventProducer;
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
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public Payment create(PaymentRequest paymentRequest) {

        //1. 결제 요청
        Payment newPayment = Payment.builder()
                .userId(paymentRequest.userId())
                .orderId(paymentRequest.orderId())
                .totalAmount(paymentRequest.amount())
                .status(PENDING)
                .build();
        Payment payment = paymentRepository.save(newPayment);

        //2. 결제 상태
        payment.updateStatus(paymentRequest.userId() == 2 ? FAILED : APPROVED);
        payment = paymentRepository.save(payment);

        //3. 결제 상태에 따라 이벤트 발행
        if (payment.getStatus() == FAILED) {
            PaymentFailed failedPayload = PaymentFailed.builder()
                    .paymentId(payment.getId())
                    .userId(payment.getUserId())
                    .orderId(payment.getOrderId())
                    .totalAmount(payment.getTotalAmount())
                    .status(FAILED.name())
                    .build();

            PaymentMessage failedMessage = PaymentMessage.builder()
                    .type(MessageType.PAYMENT_FAILED.name())
                    .payload(failedPayload)
                    .build();
            paymentEventProducer.publishPaymentCreated(failedMessage);
        } else {
            PaymentCreated approvedPayload = PaymentCreated.builder()
                    .paymentId(payment.getId())
                    .userId(payment.getUserId())
                    .orderId(payment.getOrderId())
                    .totalAmount(payment.getTotalAmount())
                    .status(APPROVED.name())
                    .build();

            PaymentMessage approvedMessage = PaymentMessage.builder()
                    .type(MessageType.PAYMENT_APPROVED.name())
                    .payload(approvedPayload)
                    .build();
            paymentEventProducer.publishPaymentCreated(approvedMessage);
        }

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
