package com.inventory.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Value("${consumer.retry.interval-ms:1000}")
    private long retryIntervalMs;

    @Value("${consumer.retry.max-attempts:3}")
    private long maxRetryAttempts;

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1)
        );
        FixedBackOff backOff = new FixedBackOff(retryIntervalMs, maxRetryAttempts);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // 재시도해도 결과가 같은 영구 실패는 즉시 DLT로 격리
        handler.addNotRetryableExceptions(
                JsonParseException.class,
                JsonMappingException.class
        );

        return handler;
    }
}
