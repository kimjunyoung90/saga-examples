package com.example.service;

import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.exception.InventoryNotFoundException;
import com.example.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Inventory deduct(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.productId())
                .orElseThrow(() -> new InventoryNotFoundException());
        inventory.deduct(inventoryRequest.quantity());
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory cancel(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException());
        inventory.cancel(quantity);
        return inventoryRepository.save(inventory);
    }

}
