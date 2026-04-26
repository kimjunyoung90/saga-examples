package com.payment.controller;

import com.payment.dto.PaymentRequest;
import com.payment.entity.Payment;
import com.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
