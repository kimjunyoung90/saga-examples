package com.payment.config;

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
        return new DefaultErrorHandler(recoverer, backOff);
    }
}
