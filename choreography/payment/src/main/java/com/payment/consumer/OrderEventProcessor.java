package com.payment.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.consumer.event.EventMessage;
import com.payment.consumer.event.OrderCreatedEvent;
import com.payment.dto.PaymentRequest;
import com.payment.idempotency.IdempotencyService;
import com.payment.idempotency.MessageHeaders;
import com.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProcessor {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;

    @Transactional(rollbackFor = Exception.class)
    public void process(ConsumerRecord<String, String> record) {
        try {
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
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse order event", e);
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
