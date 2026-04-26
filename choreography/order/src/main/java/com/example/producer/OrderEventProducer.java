package com.example.producer;

import com.example.constant.KafkaTopics;
import com.example.producer.event.OrderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderMessage message) {
        kafkaTemplate.send(KafkaTopics.ORDER_EVENTS, message.type(), message);
    }

}
