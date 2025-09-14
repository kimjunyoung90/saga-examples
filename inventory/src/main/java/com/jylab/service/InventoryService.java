package com.jylab.service;

import lombok.RequiredArgsConstructor;
import com.jylab.entity.Inventory;
import com.jylab.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory getStock(Long productId) {
        return inventoryRepository.findById(productId).orElseThrow();
    }

    public Inventory increaseInventory(Long productId) {
        Inventory inventory = inventoryRepository.findById(productId).orElseThrow();
        inventory.setQuantity(inventory.getQuantity() + 1);
        return inventoryRepository.save(inventory);
    }

    public Inventory decreaseInventory(Long productId) {
        Inventory inventory = inventoryRepository.findById(productId).orElseThrow();
        inventory.setQuantity(inventory.getQuantity() - 1);
        return inventoryRepository.save(inventory);
    }
}
