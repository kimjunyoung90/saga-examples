package com.jylab.controller;

public record DecreaseInventoryRequest(Long productId, Integer quantity) {
}
