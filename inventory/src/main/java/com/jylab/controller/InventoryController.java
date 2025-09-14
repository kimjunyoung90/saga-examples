package com.jylab.controller;

import lombok.RequiredArgsConstructor;
import com.jylab.entity.Inventory;
import com.jylab.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getStock(productId);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{productId}/increase")
    public ResponseEntity<Inventory> increaseInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.increaseInventory(productId);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{productId}/decrease")
    public ResponseEntity<Inventory> decreaseInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.decreaseInventory(productId);
        return ResponseEntity.ok(inventory);
    }
}
