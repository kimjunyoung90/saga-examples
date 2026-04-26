package com.inventory.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.constant.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private static final int BATCH_SIZE = 100;
    private static final String HEADER_MESSAGE_ID = "messageId";
    private static final String HEADER_EVENT_TYPE = "eventType";

    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${outbox.max-retry-count:5}")
    private int maxRetryCount;

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:500}")
    @Transactional
    public void publishEvent() {
        List<OutboxMessage> messages = outboxMessageRepository.findByStatusOrderByCreatedAtAsc(
                OutboxMessage.OutboxStatus.PENDING,
                PageRequest.of(0, BATCH_SIZE)
        );
        if (messages.isEmpty()) {
            return;
        }

        for (OutboxMessage message : messages) {
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        message.getTopic(),
                        null,
                        message.getMessageKey(),
                        message.getPayload()
                );
                record.headers().add(new RecordHeader(HEADER_MESSAGE_ID, message.getMessageId().getBytes(StandardCharsets.UTF_8)));
                record.headers().add(new RecordHeader(HEADER_EVENT_TYPE, message.getEventType().getBytes(StandardCharsets.UTF_8)));

                kafkaTemplate.send(record).get();
                message.markPublished();
            } catch (Exception e) {
                handlePublishFailure(message, e);
            }
        }
    }

    private void handlePublishFailure(OutboxMessage message, Exception e) {
        message.recordFailure(e.getMessage());
        if (message.getRetryCount() >= maxRetryCount) {
            message.markFailed();
            log.error("Outbox message permanently failed (retries={}): id={}, messageId={}",
                    message.getRetryCount(), message.getId(), message.getMessageId(), e);
            if (!message.getTopic().endsWith(KafkaTopics.DLQ_SUFFIX)) {
                recordDlqOutbox(message);
            }
        } else {
            log.warn("Outbox publish failed (attempt {}/{}): id={}, messageId={}",
                    message.getRetryCount(), maxRetryCount, message.getId(), message.getMessageId(), e);
        }
    }

    private void recordDlqOutbox(OutboxMessage failedMessage) {
        try {
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("originalMessageId", failedMessage.getMessageId());
            wrapper.put("originalTopic", failedMessage.getTopic());
            wrapper.put("originalEventType", failedMessage.getEventType());
            wrapper.put("originalPayload", failedMessage.getPayload());
            wrapper.put("retryCount", failedMessage.getRetryCount());
            wrapper.put("lastError", failedMessage.getLastError());
            wrapper.put("failedAt", failedMessage.getLastAttemptAt());

            String wrappedPayload = objectMapper.writeValueAsString(wrapper);

            OutboxMessage dlqRow = OutboxMessage.builder()
                    .messageId(UUID.randomUUID().toString())
                    .topic(failedMessage.getTopic() + KafkaTopics.DLQ_SUFFIX)
                    .eventType(failedMessage.getEventType())
                    .messageKey(failedMessage.getMessageKey())
                    .payload(wrappedPayload)
                    .status(OutboxMessage.OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxMessageRepository.save(dlqRow);
            log.info("Queued DLQ outbox row: originalMessageId={}, dlqTopic={}",
                    failedMessage.getMessageId(), dlqRow.getTopic());
        } catch (JsonProcessingException e) {
            log.error("Failed to wrap DLQ payload for messageId={}", failedMessage.getMessageId(), e);
        }
    }
}
