package com.jylab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient orderWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8081").build();
    }

    @Bean
    public WebClient paymentWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8082").build();
    }

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8083").build();
    }
}
