package com.inventory.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
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

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:500}")
    @Transactional
    public void publishPending() {
        List<OutboxMessage> pending = outboxMessageRepository.findByStatusOrderByCreatedAtAsc(
                OutboxMessage.OutboxStatus.PENDING,
                PageRequest.of(0, BATCH_SIZE)
        );
        if (pending.isEmpty()) {
            return;
        }

        for (OutboxMessage message : pending) {
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
                log.error("Failed to publish outbox message id={}, will retry", message.getId(), e);
            }
        }
    }
}
