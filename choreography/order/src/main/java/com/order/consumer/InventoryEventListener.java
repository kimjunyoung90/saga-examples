package com.order.consumer;

import com.order.constant.KafkaTopics;
import com.order.consumer.event.EventMessage;
import com.order.consumer.event.InventoryEventFailed;
import com.order.idempotency.IdempotencyService;
import com.order.idempotency.MessageHeaders;
import com.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class InventoryEventListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
        topics = KafkaTopics.INVENTORY_EVENTS,
        groupId = "order-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleInventoryEvent(List<ConsumerRecord<String, String>> records) throws JsonProcessingException {
        log.info("Received {} inventory events in batch", records.size());
        for (ConsumerRecord<String, String> record : records) {
            String messageId = MessageHeaders.extractMessageId(record);
            if (idempotencyService.isDuplicate(messageId)) {
                log.info("Skip duplicate inventory event messageId={}", messageId);
                continue;
            }

            EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
            switch (eventMessage.type()) {
                case INVENTORY_FAILED -> handleInventoryFailedEvent(eventMessage.payload());
            }

            idempotencyService.markProcessed(messageId, eventMessage.type().name());
        }
    }

    private void handleInventoryFailedEvent(JsonNode payload) throws JsonProcessingException {
        InventoryEventFailed inventoryEventFailed = objectMapper.readValue(payload.toString(), InventoryEventFailed.class);
        orderService.cancelOrder(inventoryEventFailed.orderId());
    }
}
