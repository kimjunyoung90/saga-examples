package com.inventory.service;

import com.inventory.constant.KafkaTopics;
import com.inventory.dto.InventoryCancelRequest;
import com.inventory.dto.InventoryRequest;
import com.inventory.entity.Inventory;
import com.inventory.exception.InventoryNotFoundException;
import com.inventory.outbox.OutboxService;
import com.inventory.producer.event.InventoryCreated;
import com.inventory.producer.event.InventoryMessage;
import com.inventory.producer.event.MessageType;
import com.inventory.repository.InventoryRepository;
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
