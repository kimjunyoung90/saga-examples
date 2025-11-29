package com.example.producer;

import com.example.producer.event.PaymentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCreated(PaymentMessage message) {
        kafkaTemplate.send("payment-events", message.type(), message);
    }
}
