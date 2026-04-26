package com.order.consumer;

import com.order.constant.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryEventProcessor processor;

    @KafkaListener(
        topics = KafkaTopics.INVENTORY_EVENTS,
        groupId = "order-service"
    )
    public void handleInventoryEvent(List<ConsumerRecord<String, String>> records) {
        log.info("Received {} inventory events in batch", records.size());
        for (int i = 0; i < records.size(); i++) {
            try {
                processor.process(records.get(i));
            } catch (Exception e) {
                throw new BatchListenerFailedException("Failed at index " + i, e, i);
            }
        }
    }
}
