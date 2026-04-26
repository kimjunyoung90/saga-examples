package com.orchestrator.client;

import com.orchestrator.dto.request.InventoryCancelRequest;
import com.orchestrator.dto.request.InventoryConfirmRequest;
import com.orchestrator.dto.request.InventoryRequest;
import com.orchestrator.dto.response.InventoryResponse;
import com.orchestrator.exception.InventoryCancelException;
import com.orchestrator.exception.InventoryFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InventoryWebClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8082";

    public Mono<InventoryResponse> reserveInventory(InventoryRequest request) {
        return webClient.post()
                .uri(baseUrl + "/inventory/reserve")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> Mono.error(new InventoryFailedException()));
    }

    public Mono<InventoryResponse> confirmInventory(InventoryConfirmRequest request) {
        return webClient.post()
                .uri(baseUrl + "/inventory/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> Mono.error(new InventoryFailedException()));
    }

    public Mono<InventoryResponse> cancelInventory(InventoryCancelRequest request) {
        return webClient.post()
                .uri(baseUrl + "/inventory/cancel")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorResume(throwable -> Mono.error(new InventoryCancelException()));
    }
}
