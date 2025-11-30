package com.example.consumer;

import com.example.consumer.event.EventMessage;
import com.example.consumer.event.OrderCreatedEvent;
import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "order-events",
            groupId = "inventory-service"
    )
    public void handleOrderEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage message = objectMapper.readValue(record.value(), EventMessage.class);
        switch (message.type()) {
            case ORDER_CREATED -> handleOrderCreatedEvent(message.payload());
        }
    }

    private void handleOrderCreatedEvent(JsonNode payload) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload.toString(), OrderCreatedEvent.class);
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .orderId(orderCreatedEvent.orderId())
                .productId(orderCreatedEvent.productId())
                .quantity(orderCreatedEvent.quantity())
                .build();
        inventoryService.reserve(inventoryRequest);
    }
}
