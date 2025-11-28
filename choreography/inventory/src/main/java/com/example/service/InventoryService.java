package com.example.service;

import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.exception.InventoryNotFoundException;
import com.example.producer.InventoryEventProducer;
import com.example.producer.event.InventoryCreatedEvent;
import com.example.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    @Transactional
    public Inventory create(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.productId())
                .orElseThrow(() -> new InventoryNotFoundException("상품 ID를 찾을 수 없습니다."));
        inventory.deduct(inventoryRequest.quantity());
        inventory = inventoryRepository.save(inventory);

        InventoryCreatedEvent event = InventoryCreatedEvent.builder()
                .inventoryId(inventory.getId())
                .productId(inventory.getProductId())
                .status("SUCCESS")
                .build();
        inventoryEventProducer.inventoryCreatedEvent(event);
        return inventory;
    }

}
