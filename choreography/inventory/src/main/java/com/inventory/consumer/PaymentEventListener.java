package com.inventory.consumer;

import com.inventory.constant.KafkaTopics;
import com.inventory.consumer.event.EventMessage;
import com.inventory.consumer.event.PaymentFailed;
import com.inventory.dto.InventoryCancelRequest;
import com.inventory.idempotency.IdempotencyService;
import com.inventory.idempotency.MessageHeaders;
import com.inventory.service.InventoryService;
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
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "inventory-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String messageId = MessageHeaders.extractMessageId(record);
        if (idempotencyService.isDuplicate(messageId)) {
            log.info("Skip duplicate payment event messageId={}", messageId);
            return;
        }

        EventMessage message = objectMapper.readValue(record.value(), EventMessage.class);

        switch (message.type()) {
            case PAYMENT_FAILED -> handlePaymentFailedEvent(message.payload());
        }

        idempotencyService.markProcessed(messageId, message.type().name());
    }

    private void handlePaymentFailedEvent(JsonNode jsonNode) throws JsonProcessingException {
        PaymentFailed paymentFailed = objectMapper.readValue(jsonNode.toString(), PaymentFailed.class);
        InventoryCancelRequest inventoryCancelRequest = InventoryCancelRequest.builder()
                .productId(paymentFailed.productId())
                .quantity(paymentFailed.quantity())
                .build();
        inventoryService.cancel(inventoryCancelRequest);
    }
}
