package com.example.producer;

import com.example.constant.KafkaTopics;
import com.example.producer.event.InventoryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void inventoryCreatedEvent(InventoryMessage message) {
        kafkaTemplate.send(KafkaTopics.INVENTORY_EVENTS, message.type(), message);
    }
}
