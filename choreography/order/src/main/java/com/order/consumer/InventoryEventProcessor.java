package com.order.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.consumer.event.EventMessage;
import com.order.consumer.event.InventoryEventFailed;
import com.order.idempotency.IdempotencyService;
import com.order.idempotency.MessageHeaders;
import com.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProcessor {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    @Transactional(rollbackFor = Exception.class)
    public void process(ConsumerRecord<String, String> record) {
        try {
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
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse inventory event", e);
        }
    }

    private void handleInventoryFailedEvent(JsonNode payload) throws JsonProcessingException {
        InventoryEventFailed inventoryEventFailed = objectMapper.readValue(payload.toString(), InventoryEventFailed.class);
        orderService.cancelOrder(inventoryEventFailed.orderId());
    }
}
