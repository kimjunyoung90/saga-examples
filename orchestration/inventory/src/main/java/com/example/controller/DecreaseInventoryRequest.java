package com.example.controller;

public record DecreaseInventoryRequest(Long productId, Integer quantity) {
}
