package com.example.producer;

import com.example.producer.event.InventoryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void inventoryCreatedEvent(InventoryMessage event) {
        kafkaTemplate.send("inventory-events", event.type(), event);
    }
}
