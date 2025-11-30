package com.example.consumer;

import com.example.consumer.event.EventMessage;
import com.example.consumer.event.OrderCreatedEvent;
import com.example.dto.InventoryRequest;
import com.example.exception.InsufficientInventoryException;
import com.example.producer.InventoryEventProducer;
import com.example.producer.event.InventoryCreated;
import com.example.producer.event.InventoryMessage;
import com.example.producer.event.MessageType;
import com.example.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final InventoryEventProducer inventoryEventProducer;

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

        try {
            inventoryService.reserve(inventoryRequest);
        } catch (InsufficientInventoryException e) {
            log.error("Insufficient inventory for order: {}", orderCreatedEvent.orderId());

            // Publish INVENTORY_FAILED event
            InventoryCreated failedPayload = InventoryCreated.builder()
                    .orderId(orderCreatedEvent.orderId())
                    .inventoryId(null)
                    .productId(orderCreatedEvent.productId())
                    .status("FAILED")
                    .build();

            InventoryMessage message = InventoryMessage.builder()
                    .type(MessageType.INVENTORY_FAILED.name())
                    .payload(failedPayload)
                    .build();

            inventoryEventProducer.inventoryCreatedEvent(message);
        }
    }
}
