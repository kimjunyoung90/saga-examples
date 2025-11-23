package com.example.client;

import com.example.dto.request.OrderRequest;
import com.example.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderWebClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8081";

    //주문 요청
    //Mono = Java의 Reactor 라이브러리에서 제공하는 핵심 타입 중 하나로, 0개 또는 1개의 데이터 항목을 비동기적으로 생성할 수 있는 반응형 스트림이다.
    public Mono<OrderResponse> createOrder(OrderRequest orderRequest) {
        return webClient.post()
                .uri(baseUrl + "/orders")
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(OrderResponse.class);
    }
}
