package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Stock;
import org.example.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{productId}")
    public Optional<Stock> getStock(@PathVariable Long productId) {
        return stockService.getStock(productId);
    }

    @PutMapping("/{productId}/increase")
    public Stock increaseStock(@PathVariable Long productId) {
        return stockService.increaseStock(productId);
    }

    @PutMapping("/{productId}/decrease")
    public Stock decreaseStock(@PathVariable Long productId) {
        return stockService.decreaseStock(productId);
    }
}
