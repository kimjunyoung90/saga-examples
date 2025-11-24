package com.example.controller;

import com.example.dto.PaymentRequest;
import com.example.entity.Payment;
import com.example.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody PaymentRequest paymentRequest) {
        Payment created = paymentService.create(paymentRequest);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/cancel/{paymentId}")
    public ResponseEntity<Void> cancel(@PathVariable Long paymentId) {
        paymentService.cancel(paymentId);
        return ResponseEntity.ok().build();
    }
}
