package com.orchestrator.service;

import com.orchestrator.client.InventoryWebClient;
import com.orchestrator.client.OrderWebClient;
import com.orchestrator.client.PaymentWebClient;
import com.orchestrator.dto.request.InventoryCancelRequest;
import com.orchestrator.dto.request.InventoryConfirmRequest;
import com.orchestrator.dto.request.InventoryRequest;
import com.orchestrator.dto.request.OrderRequest;
import com.orchestrator.dto.request.PaymentRequest;
import com.orchestrator.dto.response.OrderResponse;
import com.orchestrator.exception.InventoryFailedException;
import com.orchestrator.exception.OrderFailedException;
import com.orchestrator.exception.PaymentFailedException;
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
        try {
            //1. 주문 생성
            OrderResponse orderResponse = orderClient.createOrder(request).block();
            orderId = orderResponse.orderId();

            //2. 재고 예약
            InventoryRequest inventoryRequest = new InventoryRequest(orderId, orderResponse.productId(), orderResponse.quantity());
            inventoryClient.reserveInventory(inventoryRequest).block();

            //3. 결제 처리
            Long userId = orderResponse.userId();
            BigDecimal totalAmount = new BigDecimal(orderResponse.quantity()).multiply(orderResponse.price());
            PaymentRequest paymentRequest = new PaymentRequest(orderId, userId, totalAmount);
            paymentClient.createPayment(paymentRequest).block();

            //4. 재고 예약 확정 (영구 차감)
            inventoryClient.confirmInventory(new InventoryConfirmRequest(orderId)).block();

            //5. 주문 확정 처리
            orderClient.approveOrder(orderId).block();

        } catch (OrderFailedException orderFailedException) {
            return "FAILED_ORDER";
        } catch (InventoryFailedException inventoryFailedException) {
            //1. 주문 취소
            orderClient.cancelOrder(orderId).block();
            return "FAILED_INVENTORY";
        } catch (PaymentFailedException paymentFailedException) {
            //1. 재고 예약 취소 (orderId 기반으로 정확히 매칭)
            inventoryClient.cancelInventory(new InventoryCancelRequest(orderId)).block();

            //2. 주문 취소
            orderClient.cancelOrder(orderId).block();
            return "FAILED_PAYMENT";
        } catch (Exception e) {
            log.error("기타 오류 : {}", e.getMessage());
        }

        return "SUCCESS";
    }
}
