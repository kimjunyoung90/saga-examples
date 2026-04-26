package com.example.service;

import com.example.constant.KafkaTopics;
import com.example.dto.InventoryCancelRequest;
import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.exception.InventoryNotFoundException;
import com.example.outbox.OutboxService;
import com.example.producer.event.InventoryCreated;
import com.example.producer.event.InventoryMessage;
import com.example.producer.event.MessageType;
import com.example.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final OutboxService outboxService;

    @Transactional
    public ReserveResult reserve(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.productId())
                .orElseThrow(InventoryNotFoundException::new);

        if (inventory.getQuantity() < inventoryRequest.quantity()) {
            return ReserveResult.insufficient();
        }

        inventory.deduct(inventoryRequest.quantity());
        inventory = inventoryRepository.save(inventory);

        InventoryCreated payload = InventoryCreated.builder()
                .orderId(inventoryRequest.orderId())
                .inventoryId(inventory.getId())
                .productId(inventory.getProductId())
                .status("SUCCESS")
                .build();

        //outbox pattern
        InventoryMessage envelope = InventoryMessage.builder()
                .type(MessageType.INVENTORY_RESERVED.name())
                .payload(payload)
                .build();
        outboxService.record(
                KafkaTopics.INVENTORY_EVENTS,
                MessageType.INVENTORY_RESERVED.name(),
                String.valueOf(inventoryRequest.orderId()),
                envelope
        );

        return ReserveResult.success(inventory);
    }

    @Transactional
    public Inventory cancel(InventoryCancelRequest inventoryCancelRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryCancelRequest.productId())
                .orElseThrow(InventoryNotFoundException::new);

        inventory.cancel(inventoryCancelRequest.quantity());
        return inventoryRepository.save(inventory);
    }
}
