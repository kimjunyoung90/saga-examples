package com.example.listener;

import com.example.entity.Payment;
import com.example.event.OrderCreatedEvent;
import com.example.event.PaymentProcessedEvent;
import com.example.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order", groupId = "payment-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        //1. 결제 처리
        Payment newPayment = new Payment();
        newPayment.setOrderId(event.orderId());
        newPayment.setAmount(event.totalAmount());
        Payment payment = paymentService.create(newPayment);

        //2. 결제 성공 이벤트 발행
        kafkaTemplate.send("payment", new PaymentProcessedEvent(
                payment.getId(),
                payment.getOrderId(),
                event.productId(),
                event.quantity(),
                payment.getAmount()
        ));
    }
}
