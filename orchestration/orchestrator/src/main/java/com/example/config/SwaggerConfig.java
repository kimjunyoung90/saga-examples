package com.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orchestratorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Saga Orchestrator API")
                        .description("분산 트랜잭션을 관리하는 Saga Orchestration Pattern 구현 API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Orchestrator Service")
                                .email("orchestrator@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Orchestrator Server")
                ));
    }
}
