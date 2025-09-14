package com.jylab.controller;

import lombok.RequiredArgsConstructor;
import com.jylab.entity.Stock;
import com.jylab.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{productId}")
    public ResponseEntity<Stock> getStock(@PathVariable Long productId) {
        Stock stock = stockService.getStock(productId);
        return ResponseEntity.ok(stock);
    }

    @PutMapping("/{productId}/increase")
    public ResponseEntity<Stock> increaseStock(@PathVariable Long productId) {
        Stock stock = stockService.increaseStock(productId);
        return ResponseEntity.ok(stock);
    }

    @PutMapping("/{productId}/decrease")
    public ResponseEntity<Stock> decreaseStock(@PathVariable Long productId) {
        Stock stock = stockService.decreaseStock(productId);
        return ResponseEntity.ok(stock);
    }
}
