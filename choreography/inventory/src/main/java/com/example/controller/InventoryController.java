package com.example.controller;

import com.example.entity.Inventory;
import com.example.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<Inventory> create(@RequestBody Inventory inventory) {
        Inventory created = inventoryService.create(inventory);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> findById(@PathVariable Long productId) {
        Inventory inventory = inventoryService.findById(productId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> findAll() {
        List<Inventory> inventories = inventoryService.findAll();
        return ResponseEntity.ok(inventories);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Inventory> update(@PathVariable Long productId, @RequestBody Inventory inventory) {
        Inventory updated = inventoryService.update(productId, inventory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        inventoryService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/decrease")
    public ResponseEntity<Void> decreaseQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.decreaseQuantity(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/increase")
    public ResponseEntity<Void> increaseQuantity(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.increaseQuantity(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
