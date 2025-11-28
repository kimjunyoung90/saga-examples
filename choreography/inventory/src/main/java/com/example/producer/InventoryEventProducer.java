package com.example.producer;

import com.example.producer.event.InventoryCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void inventoryCreatedEvent(InventoryCreatedEvent event) {
        kafkaTemplate.send("inventory-events", String.valueOf(event.inventoryId()), event);
    }
}
