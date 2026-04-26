package com.inventory.controller;

import com.inventory.dto.InventoryCancelRequest;
import com.inventory.dto.InventoryConfirmRequest;
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

    @PostMapping("/reserve")
    public ResponseEntity<Inventory> reserve(@RequestBody InventoryRequest request) {
        Inventory reserved = inventoryService.reserve(request);
        return ResponseEntity.ok(reserved);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Inventory> confirm(@RequestBody InventoryConfirmRequest request) {
        Inventory confirmed = inventoryService.confirm(request.orderId());
        return ResponseEntity.ok(confirmed);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Inventory> cancel(@RequestBody InventoryCancelRequest request) {
        Inventory canceled = inventoryService.cancelByOrderId(request.orderId());
        return ResponseEntity.ok(canceled);
    }
}
