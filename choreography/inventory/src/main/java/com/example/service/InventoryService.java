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

    public Inventory findById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));
    }

    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public Inventory update(Long id, Inventory inventory) {
        Inventory existingInventory = findById(id);
        existingInventory.setProductId(inventory.getProductId());
        existingInventory.setQuantity(inventory.getQuantity());
        return inventoryRepository.save(existingInventory);
    }

    @Transactional
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public void decreaseQuantity(Long productId, Integer quantity) {
        Inventory inventory = findByProductId(productId);
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void increaseQuantity(Long productId, Integer quantity) {
        Inventory inventory = findByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}
