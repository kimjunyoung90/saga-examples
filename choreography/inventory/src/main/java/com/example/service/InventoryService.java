package com.example.service;

import com.example.entity.Inventory;
import com.example.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Inventory create(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory findById(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + productId));
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public Inventory update(Long productId, Inventory inventory) {
        Inventory existingInventory = findById(productId);
        existingInventory.setQuantity(inventory.getQuantity());
        return inventoryRepository.save(existingInventory);
    }

    @Transactional
    public void delete(Long productId) {
        inventoryRepository.deleteById(productId);
    }

    @Transactional
    public void decreaseQuantity(Long productId, Integer quantity) {
        Inventory inventory = findById(productId);
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void increaseQuantity(Long productId, Integer quantity) {
        Inventory inventory = findById(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}
