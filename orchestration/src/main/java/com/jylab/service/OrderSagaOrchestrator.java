package com.jylab.service;

import com.jylab.dto.request.InventoryRequest;
import com.jylab.dto.request.OrderRequest;
import com.jylab.dto.request.PaymentRequest;
import com.jylab.dto.response.InventoryResponse;
import com.jylab.dto.response.OrderResponse;
import com.jylab.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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
        List<InventoryResponse> inventoryResponses = inventoryClient.put()
                .uri("/inventory/decrease")
                .bodyValue(orderResponse.orderItems().stream().map(orderItem -> {
                    InventoryRequest inventoryRequest = new InventoryRequest(orderItem.productId(), orderItem.quantity());
                    return inventoryRequest;
                }).toList())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();

        if (inventoryResponses == null || inventoryResponses.isEmpty()) {
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
