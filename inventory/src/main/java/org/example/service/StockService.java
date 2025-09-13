package org.example.service;

import org.example.entity.Stock;
import org.example.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public Optional<Stock> getStock(Long productId) {
        return stockRepository.findById(productId);
    }

    public Stock increaseStock(Long productId) {
        Stock stock = stockRepository.findById(productId).orElse(new Stock());
        stock.setQuantity(stock.getQuantity() + 1);
        Stock newStock = stockRepository.save(stock);
        return newStock;
    }


    public Stock decreaseStock(Long productId) {
        Stock stock = stockRepository.findById(productId).orElse(new Stock());
        stock.setQuantity(stock.getQuantity() - 1);
        Stock newStock = stockRepository.save(stock);
        return newStock;
    }
}
