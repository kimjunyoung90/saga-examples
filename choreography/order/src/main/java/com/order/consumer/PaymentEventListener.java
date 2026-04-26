package com.order.consumer;

import com.order.constant.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentEventProcessor processor;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "order-service"
    )
    public void handlePaymentEvent(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        log.info("Received {} payment events in batch", records.size());
        for (int i = 0; i < records.size(); i++) {
            try {
                processor.process(records.get(i));
            } catch (Exception e) {
                throw new BatchListenerFailedException("Failed at index " + i, e, i);
            }
        }
        ack.acknowledge();
    }
}
