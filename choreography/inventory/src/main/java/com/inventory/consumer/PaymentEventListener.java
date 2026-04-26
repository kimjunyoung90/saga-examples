package com.inventory.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.constant.KafkaTopics;
import com.inventory.consumer.event.EventMessage;
import com.inventory.consumer.event.PaymentApproved;
import com.inventory.consumer.event.PaymentFailed;
import com.inventory.idempotency.IdempotencyService;
import com.inventory.idempotency.MessageHeaders;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "inventory-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentEvent(List<ConsumerRecord<String, String>> records) throws JsonProcessingException {
        log.info("Received {} payment events in batch", records.size());
        for (ConsumerRecord<String, String> record : records) {
            String messageId = MessageHeaders.extractMessageId(record);
            if (idempotencyService.isDuplicate(messageId)) {
                log.info("Skip duplicate payment event messageId={}", messageId);
                continue;
            }

            EventMessage message = objectMapper.readValue(record.value(), EventMessage.class);
            switch (message.type()) {
                case PAYMENT_APPROVED -> handlePaymentApprovedEvent(message.payload());
                case PAYMENT_FAILED -> handlePaymentFailedEvent(message.payload());
            }

            idempotencyService.markProcessed(messageId, message.type().name());
        }
    }

    private void handlePaymentApprovedEvent(JsonNode payload) throws JsonProcessingException {
        PaymentApproved approved = objectMapper.readValue(payload.toString(), PaymentApproved.class);
        inventoryService.confirm(approved.orderId());
    }

    private void handlePaymentFailedEvent(JsonNode payload) throws JsonProcessingException {
        PaymentFailed failed = objectMapper.readValue(payload.toString(), PaymentFailed.class);
        inventoryService.cancelByOrderId(failed.orderId());
    }
}
