package com.example.service;

import com.example.dto.request.InventoryRequest;
import com.example.dto.request.OrderRequest;
import com.example.dto.request.PaymentRequest;
import com.example.dto.response.InventoryResponse;
import com.example.dto.response.OrderResponse;
import com.example.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestrationService {

    @Qualifier("orderClient")
    private final WebClient orderClient;
    @Qualifier("paymentClient")
    private final WebClient paymentClient;
    @Qualifier("inventoryClient")
    private final WebClient inventoryClient;

    public String orderProcess(OrderRequest request) {
        String transactionId = UUID.randomUUID().toString();
        //1. 주문 요청
        OrderResponse orderResponse = orderClient.post()
                .uri("/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();

        //2. 결제 요청
        Long orderId = orderResponse.orderId();
        Long userId = orderResponse.userId();
        BigDecimal totalAmount = new BigDecimal(orderResponse.quantity()).multiply(orderResponse.price());
        PaymentResponse paymentResponse = paymentClient.post()
                .uri("/payment")
                .bodyValue(new PaymentRequest(orderId, userId, totalAmount))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(PaymentResponse.class)
                .block();

        //3. 재고
        InventoryResponse inventoryResponse = inventoryClient.put()
                .uri("/inventory/decrease")
                .bodyValue(new InventoryRequest(orderResponse.orderId(), orderResponse.productId(), orderResponse.quantity()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(InventoryResponse.class)
                .block();

        return "success";
    }
}
