package com.example.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "inventory-events",
        groupId = "order-service"
    )
    /**
     * ConsumerRecord<K, V> {
     *     String topic;           // 토픽 이름
     *     int partition;          // 파티션 번호
     *     long offset;            // 오프셋
     *     long timestamp;         // 타임스탬프
     *     K key;                  // 메시지 키
     *     V value;                // 메시지 값
     *     Headers headers;        // 헤더 정보
     * }
     */
    public void handleInventoryEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        InventoryEvent inventoryEvent = objectMapper.readValue(record.value(), InventoryEvent.class);
    }
}
