package com.inventory.controller;

import com.inventory.dto.InventoryCancelRequest;
import com.inventory.dto.InventoryRequest;
import com.inventory.entity.Inventory;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/deduct")
    public ResponseEntity<Inventory> deduct(@RequestBody InventoryRequest inventoryRequest) {
        Inventory created = inventoryService.deduct(inventoryRequest);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Inventory> cancel(@RequestBody InventoryCancelRequest inventoryRequest) {
        Inventory canceled = inventoryService.cancel(inventoryRequest.productId(), inventoryRequest.quantity());
        return ResponseEntity.ok(canceled);
    }
}
