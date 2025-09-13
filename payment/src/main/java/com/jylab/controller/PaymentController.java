package com.jylab.controller;

import lombok.RequiredArgsConstructor;
import com.jylab.entity.Payment;
import com.jylab.service.PaymentService;
import org.springframework.web.bind.annotation.*;

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
    public Payment createPayment(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(paymentRequest);
    }

    @DeleteMapping("/{paymentId}")
    public void cancelPayment(@PathVariable Long paymentId) {
        paymentService.cancelPayment(paymentId);
    }
}
