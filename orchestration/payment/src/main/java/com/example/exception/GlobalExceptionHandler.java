package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<Void> handlePaymentFailedException(PaymentFailedException ex) {
        return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
    }
}
