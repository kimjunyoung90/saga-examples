package com.example.consumer;

import com.example.constant.KafkaTopics;
import com.example.consumer.event.EventMessage;
import com.example.consumer.event.OrderCreatedEvent;
import com.example.dto.PaymentRequest;
import com.example.idempotency.IdempotencyService;
import com.example.idempotency.MessageHeaders;
import com.example.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_EVENTS,
            groupId = "payment-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String messageId = MessageHeaders.extractMessageId(record);
        if (idempotencyService.isDuplicate(messageId)) {
            log.info("Skip duplicate order event messageId={}", messageId);
            return;
        }

        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case ORDER_CREATED -> handleOrderCreatedEvent(eventMessage.payload());
        }

        idempotencyService.markProcessed(messageId, eventMessage.type().name());
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
