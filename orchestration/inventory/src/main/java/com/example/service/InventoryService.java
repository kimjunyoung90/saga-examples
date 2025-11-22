package com.example.service;

import com.example.dto.InventoryRequest;
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
    public Inventory create(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.productId()).orElseThrow(() -> new IllegalArgumentException("상품 ID를 찾을 수 없습니다."));
        inventory.deduct(inventoryRequest.quantity());
        return inventoryRepository.save(inventory);
    }

}
