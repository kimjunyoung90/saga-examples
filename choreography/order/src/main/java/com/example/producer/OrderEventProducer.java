package com.example.producer;

import com.example.producer.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 주문 생성 이벤트 발행
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order-events", String.valueOf(event.orderId()), event);
    }

    public void publishOrderCanceled() {

    }

}
