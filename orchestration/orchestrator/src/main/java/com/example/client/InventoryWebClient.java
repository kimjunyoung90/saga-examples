package com.example.client;

import com.example.dto.request.InventoryRequest;
import com.example.dto.response.InventoryResponse;
import com.example.exception.InventoryCancelException;
import com.example.exception.InventoryFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InventoryWebClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8082";

    //재고 차감
    public Mono<InventoryResponse> reserveInventory(InventoryRequest inventoryRequest) {
        return webClient.post()
                .uri(baseUrl + "/inventory/deduct")
                .bodyValue(inventoryRequest)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> {
                    // WebClient의 모든 에러를 InventoryFailedException으로 변환
                    return Mono.error(new InventoryFailedException("재고 차감 실패: " + throwable.getMessage()));
                });
    }

    public Mono<InventoryResponse> cancelInventory(InventoryRequest inventoryRequest) {
        return webClient.post()
                .uri(baseUrl + "/inventory/cancel")
                .bodyValue(inventoryRequest)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> {
                    return Mono.error(new InventoryCancelException("재고 원복 실패"));
                });
    }
}
