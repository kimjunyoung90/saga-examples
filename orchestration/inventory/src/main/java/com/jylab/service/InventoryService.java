package com.jylab.service;

import com.jylab.controller.DecreaseInventoryRequest;
import lombok.RequiredArgsConstructor;
import com.jylab.entity.Inventory;
import com.jylab.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Inventory> decreaseInventories(List<DecreaseInventoryRequest> requests) {
        List<Inventory> updated = new ArrayList<>();

        // 먼저 모든 요청에 대해 재고 충분성을 검증
        for(DecreaseInventoryRequest request : requests) {
            Inventory inventory = inventoryRepository.findById(request.productId()).orElseThrow();
            if(inventory.getQuantity() < request.quantity()) {
                throw new RuntimeException("재고 부족");
            }
        }

        // 모든 검증이 통과하면 재고 감소 실행
        for(DecreaseInventoryRequest request : requests) {
            Inventory inventory = inventoryRepository.findById(request.productId()).orElseThrow();
            inventory.setQuantity(inventory.getQuantity() - request.quantity());
            Inventory newInventory = inventoryRepository.save(inventory);
            updated.add(newInventory);
        }
        return updated;
    }
}
