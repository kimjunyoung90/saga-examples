package com.example.controller;

import com.example.dto.InventoryRequest;
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
    public ResponseEntity<Inventory> create(@RequestBody InventoryRequest inventoryRequest) {
        Inventory created = inventoryService.create(inventoryRequest);
        return ResponseEntity.ok(created);
    }
}
