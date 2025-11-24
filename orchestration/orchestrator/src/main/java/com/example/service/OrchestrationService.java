package com.example.service;

import com.example.client.InventoryWebClient;
import com.example.client.OrderWebClient;
import com.example.client.PaymentWebClient;
import com.example.dto.request.InventoryRequest;
import com.example.dto.request.OrderRequest;
import com.example.dto.request.PaymentRequest;
import com.example.dto.response.InventoryResponse;
import com.example.dto.response.OrderResponse;
import com.example.dto.response.PaymentResponse;
import com.example.exception.InventoryFailedException;
import com.example.exception.PaymentFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final OrderWebClient orderClient;
    private final PaymentWebClient paymentClient;
    private final InventoryWebClient inventoryClient;

    public String orderProcess(OrderRequest request) {
        String transactionId = UUID.randomUUID().toString();
        try {
            //1. 주문 요청
            OrderResponse orderResponse = orderClient.createOrder(request).block();

            //2. 결제 요청
            Long orderId = orderResponse.orderId();
            Long userId = orderResponse.userId();
            BigDecimal totalAmount = new BigDecimal(orderResponse.quantity()).multiply(orderResponse.price());
            PaymentRequest paymentRequest = new PaymentRequest(orderId, userId, totalAmount);
            PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequest).block();

            //3. 재고
            InventoryRequest inventoryRequest = new InventoryRequest(orderResponse.orderId(), orderResponse.productId(), orderResponse.quantity());
            InventoryResponse inventoryResponse = inventoryClient.reserveInventory(inventoryRequest).block();
        } catch (PaymentFailedException paymentFailedException) {
            //1. 주문 취소
        } catch (InventoryFailedException inventoryFailedException) {
            //1. 결제 취소
            //2. 주문 취소
        } catch (Exception e) {
            //1. nothing
        }


        return "success";
    }
}
