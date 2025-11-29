package com.example.client;

import com.example.dto.request.PaymentRequest;
import com.example.dto.response.PaymentResponse;
import com.example.exception.PaymentFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PaymentWebClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8082";

    //결제 요청
    public Mono<PaymentResponse> createPayment(PaymentRequest paymentRequest) {
        return webClient.post()
                .uri(baseUrl + "/payments")
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .onErrorResume(throwable -> {
                    return Mono.error(new PaymentFailedException("결재 실패: " + throwable.getMessage()));
                });
    }

    public Mono<Void> cancelPayment(Long paymentId) {
        return webClient.post()
                .uri(baseUrl + "/payments/{paymentId}/cancel", paymentId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .bodyToMono(Void.class);
    }
}
