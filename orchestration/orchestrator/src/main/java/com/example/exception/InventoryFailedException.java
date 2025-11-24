package com.example.exception;

public class InventoryFailedException extends RuntimeException{
    public InventoryFailedException(String message) {
        super(message);
    }
}
