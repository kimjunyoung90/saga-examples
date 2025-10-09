package com.example.controller;

import lombok.RequiredArgsConstructor;
import com.example.entity.Payment;
import com.example.service.PaymentService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        Payment payment = paymentService.createPayment(paymentRequest);
        return ResponseEntity
                .created(URI.create("/payment/" + payment.getId()))
                .body(payment);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
