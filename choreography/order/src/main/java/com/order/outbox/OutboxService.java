package com.order.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public String record(String topic, String eventType, String messageKey, Object payload) {
        try {
            String messageId = UUID.randomUUID().toString();
            String json = objectMapper.writeValueAsString(payload);

            OutboxMessage message = OutboxMessage.builder()
                    .messageId(messageId)
                    .topic(topic)
                    .eventType(eventType)
                    .messageKey(messageKey)
                    .payload(json)
                    .status(OutboxMessage.OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxMessageRepository.save(message);
            return messageId;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
