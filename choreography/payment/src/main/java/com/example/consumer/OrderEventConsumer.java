package com.example.consumer;

import com.example.consumer.event.EventMessage;
import com.example.consumer.event.OrderCreatedEvent;
import com.example.dto.PaymentRequest;
import com.example.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "order-events",
            groupId = "payment-service"
    )
    public void handleOrderEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case ORDER_CREATED -> handleOrderCreatedEvent(eventMessage.payload());
        }
    }

    private void handleOrderCreatedEvent(JsonNode payload) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload.toString(), OrderCreatedEvent.class);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderCreatedEvent.orderId())
                .userId(orderCreatedEvent.userId())
                .amount(orderCreatedEvent.amount())
                .build();
        paymentService.create(paymentRequest);
    }
}
