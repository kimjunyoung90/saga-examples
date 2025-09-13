package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Stock;
import org.example.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Optional<Stock> getStock(Long productId) {
        return stockRepository.findById(productId);
    }

    public Stock increaseStock(Long productId) {
        Stock stock = stockRepository.findById(productId).orElseThrow();
        stock.setQuantity(stock.getQuantity() + 1);
        return stockRepository.save(stock);
    }

    public Stock decreaseStock(Long productId) {
        Stock stock = stockRepository.findById(productId).orElseThrow();
        stock.setQuantity(stock.getQuantity() - 1);
        return stockRepository.save(stock);
    }
}
