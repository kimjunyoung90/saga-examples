package com.example.service;

import com.example.client.InventoryWebClient;
import com.example.client.OrderWebClient;
import com.example.client.PaymentWebClient;
import com.example.dto.request.InventoryRequest;
import com.example.dto.request.OrderRequest;
import com.example.dto.request.PaymentRequest;
import com.example.dto.response.InventoryResponse;
import com.example.dto.response.OrderResponse;
import com.example.exception.InventoryFailedException;
import com.example.exception.OrderFailedException;
import com.example.exception.PaymentFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final OrderWebClient orderClient;
    private final PaymentWebClient paymentClient;
    private final InventoryWebClient inventoryClient;

    public String orderSagaTransaction(OrderRequest request) {
        Long orderId = null;
        Long paymentId = null;
        try {
            //1. 주문 요청
            OrderResponse orderResponse = orderClient.createOrder(request).block();

            //2. 재고 차감
            InventoryRequest inventoryRequest = new InventoryRequest(orderResponse.productId(), orderResponse.quantity());
            inventoryClient.reserveInventory(inventoryRequest).block();

            //3. 결제
            orderId = orderResponse.orderId();
            Long userId = orderResponse.userId();
            BigDecimal totalAmount = new BigDecimal(orderResponse.quantity()).multiply(orderResponse.price());
            PaymentRequest paymentRequest = new PaymentRequest(orderId, userId, totalAmount);
            paymentClient.createPayment(paymentRequest).block();

        } catch (OrderFailedException orderFailedException) {
            return "FAILED_ORDER";
        } catch (InventoryFailedException inventoryFailedException) {
            //1. 주문 취소
            orderClient.cancelOrder(orderId).block();
            return "FAILED_INVENTORY";
        } catch (PaymentFailedException paymentFailedException) {
            //1. 재고 원복
            inventoryClient.cancelInventory(new InventoryRequest(request.productId(), request.quantity())).block();

            //2. 주문 취소
            orderClient.cancelOrder(orderId).block();
            return "FAILED_PAYMENT";
        } catch (Exception e) {
            //로그 적재
            log.error("보상 트랜잭션 오류 : {}", e.getMessage());
        }

        return "SUCCESS";
    }
}
