package com.example.consumer;

import com.example.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(
        topics = "inventory-events",
        groupId = "order-service"
    )
    public void handleInventoryEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case INVENTORY_FAILED -> handleInventoryFailedEvent(eventMessage.payload());
        }
    }

    private void handleInventoryFailedEvent(JsonNode payload) throws JsonProcessingException {
        InventoryEventFailed inventoryEventFailed = objectMapper.readValue(payload.toString(), InventoryEventFailed.class);
        orderService.cancelOrder(inventoryEventFailed.orderId());
    }
}
