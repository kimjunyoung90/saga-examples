package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Payment;
import org.example.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public Payment getPayment(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PostMapping
    public Payment createPayment(@RequestBody PaymentRequeset paymentRequeset) {
        return paymentService.createPayment(paymentRequeset);
    }

    @DeleteMapping("/{paymentId}")
    public void cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
    }
}
