package com.example.client;

import com.example.dto.request.InventoryRequest;
import com.example.dto.response.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
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
        return webClient.put()
                .uri(baseUrl + "/inventory")
                .bodyValue(inventoryRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(InventoryResponse.class);
    }
}
