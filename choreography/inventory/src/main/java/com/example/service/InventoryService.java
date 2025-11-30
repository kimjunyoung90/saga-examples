package com.example.service;

import com.example.dto.InventoryCancelRequest;
import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.exception.InventoryNotFoundException;
import com.example.producer.InventoryEventProducer;
import com.example.producer.event.MessageType;
import com.example.producer.event.InventoryCreated;
import com.example.producer.event.InventoryMessage;
import com.example.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    /**
     * 내부적으로 간단하게 그냥 차감
     */
    @Transactional
    public Inventory reserve(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.productId())
                .orElseThrow(() -> new InventoryNotFoundException());
        inventory.deduct(inventoryRequest.quantity());
        inventory = inventoryRepository.save(inventory);

        InventoryCreated payload = InventoryCreated.builder()
                .orderId(inventoryRequest.orderId())
                .inventoryId(inventory.getId())
                .productId(inventory.getProductId())
                .status("SUCCESS")
                .build();
        InventoryMessage message = InventoryMessage.builder()
                .type(MessageType.INVENTORY_RESERVED.name())
                .payload(payload)
                .build();
        inventoryEventProducer.inventoryCreatedEvent(message);
        return inventory;
    }

    public Inventory cancel(InventoryCancelRequest inventoryCancelRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryCancelRequest.productId())
                .orElseThrow(() -> new InventoryNotFoundException());

        inventory.cancel(inventoryCancelRequest.quantity());
        return inventoryRepository.save(inventory);
    }

}
