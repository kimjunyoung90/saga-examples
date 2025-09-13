package com.jylab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final WebClient orderClient;
    private final WebClient paymentClient;
    private final WebClient inventoryClient;

    public String process() {
        //1. 주문

        //2. 결제

        //3. 재고
        return null;
    }
}
