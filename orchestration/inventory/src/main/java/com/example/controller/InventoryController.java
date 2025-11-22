package com.example.controller;

import com.example.dto.InventoryRequest;
import com.example.entity.Inventory;
import com.example.service.InventoryService;
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

    @PostMapping
    public ResponseEntity<Inventory> create(@RequestBody InventoryRequest inventoryRequest) {
        Inventory created = inventoryService.create(inventoryRequest);
        return ResponseEntity.ok(created);
    }
}
