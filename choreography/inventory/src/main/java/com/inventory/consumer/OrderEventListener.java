package com.inventory.consumer;

import com.inventory.constant.KafkaTopics;
import com.inventory.consumer.event.EventMessage;
import com.inventory.consumer.event.OrderCreatedEvent;
import com.inventory.dto.InventoryRequest;
import com.inventory.idempotency.IdempotencyService;
import com.inventory.idempotency.MessageHeaders;
import com.inventory.outbox.OutboxService;
import com.inventory.producer.event.InventoryCreated;
import com.inventory.producer.event.InventoryMessage;
import com.inventory.producer.event.MessageType;
import com.inventory.service.InventoryService;
import com.inventory.service.ReserveResult;
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
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final OutboxService outboxService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_EVENTS,
            groupId = "inventory-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String messageId = MessageHeaders.extractMessageId(record);
        if (idempotencyService.isDuplicate(messageId)) {
            log.info("Skip duplicate order event messageId={}", messageId);
            return;
        }

        EventMessage message = objectMapper.readValue(record.value(), EventMessage.class);
        switch (message.type()) {
            case ORDER_CREATED -> handleOrderCreatedEvent(message.payload());
        }

        idempotencyService.markProcessed(messageId, message.type().name());
    }

    private void handleOrderCreatedEvent(JsonNode payload) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload.toString(), OrderCreatedEvent.class);
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .orderId(orderCreatedEvent.orderId())
                .productId(orderCreatedEvent.productId())
                .quantity(orderCreatedEvent.quantity())
                .build();

        ReserveResult result = inventoryService.reserve(inventoryRequest);
        if (!result.isSuccess()) {
            log.warn("Insufficient inventory for orderId={}", orderCreatedEvent.orderId());
            publishInventoryFailed(orderCreatedEvent);
        }
    }

    private void publishInventoryFailed(OrderCreatedEvent orderCreatedEvent) {
        InventoryCreated failedPayload = InventoryCreated.builder()
                .orderId(orderCreatedEvent.orderId())
                .inventoryId(null)
                .productId(orderCreatedEvent.productId())
                .status("FAILED")
                .build();

        InventoryMessage envelope = InventoryMessage.builder()
                .type(MessageType.INVENTORY_FAILED.name())
                .payload(failedPayload)
                .build();

        outboxService.record(
                KafkaTopics.INVENTORY_EVENTS,
                MessageType.INVENTORY_FAILED.name(),
                String.valueOf(orderCreatedEvent.orderId()),
                envelope
        );
    }
}
