package com.example.controller;

import com.example.dto.request.OrderRequest;
import com.example.service.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga")
@RequiredArgsConstructor
public class OrchestrationController {

    private final OrderSagaOrchestrator orchestrator;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        String result = orchestrator.process(request);
        return ResponseEntity.ok(result);
    }
}
