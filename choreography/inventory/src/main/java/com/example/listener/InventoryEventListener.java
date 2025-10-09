package com.example.listener;

import com.example.event.PaymentProcessedEvent;
import com.example.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment", groupId = "inventory-service-group")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        //1. 재고 차감
        inventoryService.decreaseQuantity(event.productId(), event.quantity());
    }
}
