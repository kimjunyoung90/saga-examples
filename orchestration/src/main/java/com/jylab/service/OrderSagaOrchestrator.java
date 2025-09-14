package com.jylab.service;

import com.jylab.dto.request.InventoryRequest;
import com.jylab.dto.request.OrderRequest;
import com.jylab.dto.request.PaymentRequest;
import com.jylab.dto.response.InventoryResponse;
import com.jylab.dto.response.OrderItem;
import com.jylab.dto.response.OrderResponse;
import com.jylab.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
                .bodyToMono(PaymentResponse.class)
                .block();

        if (paymentResponse == null || paymentResponse.paymentId() == null) {
            //2-1. 주문 취소
            orderClient.delete()
                    .uri("/orders/{orderId}", orderResponse.orderId())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }

        //3. 재고
        for (OrderItem orderItem : orderResponse.orderItems()) {
            InventoryResponse inventoryResponse = inventoryClient.put()
                    .uri("/inventory/{productId}/decrease", orderItem.productId())
                    .retrieve()
                    .bodyToMono(InventoryResponse.class)
                    .block();

            if (inventoryResponse == null || inventoryResponse.productId() == null) {
                //3-1. 결제 취소
                ResponseEntity<Void> response = paymentClient.delete()
                        .uri("/payment/{paymentId}", paymentResponse.paymentId())
                        .retrieve()
                        .toBodilessEntity()
                        .block();

                if(!response.getStatusCode().is2xxSuccessful()) {
                    //결제 취소 실패 시 실행할 로직 수행
                }

                //3-2. 주문 취소
                orderClient.delete()
                        .uri("/orders/{orderId}", orderResponse.orderId())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            }
        }

        return "success";
    }
}
