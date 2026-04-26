package com.order.outbox;

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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private static final int BATCH_SIZE = 100;
    private static final String HEADER_MESSAGE_ID = "messageId";
    private static final String HEADER_EVENT_TYPE = "eventType";

    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

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
        } else {
            log.warn("Outbox publish failed (attempt {}/{}): id={}, messageId={}",
                    message.getRetryCount(), maxRetryCount, message.getId(), message.getMessageId(), e);
        }
    }
}
