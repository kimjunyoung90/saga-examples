package com.example.controller;

import com.example.dto.CancelInventoryRequest;
import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Inventory> cancel(@RequestBody CancelInventoryRequest inventoryRequest) {
        Inventory canceled = inventoryService.cancel(inventoryRequest.productId(), inventoryRequest.quantity());
        return ResponseEntity.ok(canceled);
    }
}
