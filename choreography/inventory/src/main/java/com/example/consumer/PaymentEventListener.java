package com.example.consumer;

import com.example.consumer.event.EventMessage;
import com.example.consumer.event.PaymentFailed;
import com.example.dto.InventoryCancelRequest;
import com.example.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "payment-events",
            groupId = "inventory-service"
    )
    public void handlePaymentEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage message = objectMapper.readValue(record.value(), EventMessage.class);

        switch (message.type()) {
            case PAYMENT_FAILED -> handlePaymentFailedEvent(message.payload());
        }
    }

    private void handlePaymentFailedEvent(JsonNode jsonNode) throws JsonProcessingException {
        PaymentFailed paymentFailed = objectMapper.readValue(jsonNode.toString(), PaymentFailed.class);
        InventoryCancelRequest inventoryCancelRequest = InventoryCancelRequest.builder()
                .productId(paymentFailed.orderId())
                .quantity(paymentFailed.quantity())
                .build();
        inventoryService.cancel(inventoryCancelRequest);
    }
}
