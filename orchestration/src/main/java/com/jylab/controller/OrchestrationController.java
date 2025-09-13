package com.jylab.controller;

import com.jylab.service.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestrationController {

    private final OrderSagaOrchestrator orchestrator;

    @GetMapping("/order")
    public ResponseEntity<String> createOrder() {
        String result = orchestrator.process();
        return ResponseEntity.ok(result);
    }
}
