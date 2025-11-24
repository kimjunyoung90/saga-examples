package com.example.client;

import com.example.dto.request.InventoryRequest;
import com.example.dto.response.InventoryResponse;
import com.example.exception.InventoryFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InventoryWebClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8083";

    //재고 예약
    public Mono<InventoryResponse> reserveInventory(InventoryRequest inventoryRequest) {
        return webClient.post()
                .uri(baseUrl + "/inventory")
                .bodyValue(inventoryRequest)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> {
                    // WebClient의 모든 에러를 InventoryFailedException으로 변환
                    return Mono.error(new InventoryFailedException("재고 예약 실패: " + throwable.getMessage()));
                });
    }
}
