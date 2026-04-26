package com.example.consumer;

import com.example.constant.KafkaTopics;
import com.example.consumer.event.EventMessage;
import com.example.consumer.event.InventoryEventFailed;
import com.example.idempotency.IdempotencyService;
import com.example.idempotency.MessageHeaders;
import com.example.service.OrderService;
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
public class InventoryEventListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
        topics = KafkaTopics.INVENTORY_EVENTS,
        groupId = "order-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleInventoryEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String messageId = MessageHeaders.extractMessageId(record);
        if (idempotencyService.isDuplicate(messageId)) {
            log.info("Skip duplicate inventory event messageId={}", messageId);
            return;
        }

        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case INVENTORY_FAILED -> handleInventoryFailedEvent(eventMessage.payload());
        }

        idempotencyService.markProcessed(messageId, eventMessage.type().name());
    }

    private void handleInventoryFailedEvent(JsonNode payload) throws JsonProcessingException {
        InventoryEventFailed inventoryEventFailed = objectMapper.readValue(payload.toString(), InventoryEventFailed.class);
        orderService.cancelOrder(inventoryEventFailed.orderId());
    }
}
