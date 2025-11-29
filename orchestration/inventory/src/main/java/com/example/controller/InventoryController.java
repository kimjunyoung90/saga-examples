package com.example.controller;

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

    @PutMapping
    public ResponseEntity<Inventory> reserve(@RequestBody InventoryRequest inventoryRequest) {
        Inventory created = inventoryService.reserve(inventoryRequest);
        return ResponseEntity.ok(created);
    }
}
