package com.payment.consumer;

import com.payment.constant.KafkaTopics;
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
public class OrderEventConsumer {

    private final OrderEventProcessor processor;

    @KafkaListener(
            topics = KafkaTopics.ORDER_EVENTS,
            groupId = "payment-service"
    )
    public void handleOrderEvent(List<ConsumerRecord<String, String>> records) {
        log.info("Received {} order events in batch", records.size());
        for (int i = 0; i < records.size(); i++) {
            try {
                processor.process(records.get(i));
            } catch (Exception e) {
                throw new BatchListenerFailedException("Failed at index " + i, e, i);
            }
        }
    }
}
