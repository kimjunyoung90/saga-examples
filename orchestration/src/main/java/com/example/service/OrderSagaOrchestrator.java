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

@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    @Qualifier("orderClient")
    private final WebClient orderClient;
    @Qualifier("paymentClient")
    private final WebClient paymentClient;
    @Qualifier("inventoryClient")
    private final WebClient inventoryClient;

    public String process(OrderRequest request) {
        //1. 주문
        OrderResponse orderResponse = orderClient.post()
                .uri("/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderResponse.class)
                .block();

        if (orderResponse == null || orderResponse.orderId() == null) {
            return "fail";
        }

        //2. 결제
        PaymentResponse paymentResponse = paymentClient.post()
                .uri("/payment")
                .bodyValue(new PaymentRequest(orderResponse.orderId(), orderResponse.totalAmount()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(PaymentResponse.class)
                .block();

        if (paymentResponse == null || paymentResponse.paymentId() == null) {

            //2-1. 주문 취소
            orderClient.delete()
                    .uri("/orders/{orderId}", orderResponse.orderId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return "fail";
        }

        //3. 재고
        InventoryResponse inventoryResponse = inventoryClient.put()
                .uri("/inventory/decrease")
                .bodyValue(new InventoryRequest(orderResponse.productId(), orderResponse.quantity()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(InventoryResponse.class)
                .block();

        if (inventoryResponse == null) {
            //3-1. 결제 취소
            paymentClient.delete()
                    .uri("/payment/{paymentId}", paymentResponse.paymentId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            //3-2. 주문 취소
            orderClient.delete()
                    .uri("/orders/{orderId}", orderResponse.orderId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return "fail";
        }

        return "success";
    }
}
