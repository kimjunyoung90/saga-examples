package com.example.exception;

public class InventoryCancelException extends RuntimeException{
    public InventoryCancelException(String message) {
        super(message);
    }
}
