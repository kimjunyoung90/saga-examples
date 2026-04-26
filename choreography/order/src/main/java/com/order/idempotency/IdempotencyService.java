package com.order.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ProcessedEventRepository processedEventRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isDuplicate(String messageId) {
        return messageId != null && processedEventRepository.existsById(messageId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void markProcessed(String messageId, String eventType) {
        if (messageId == null) {
            return;
        }
        processedEventRepository.save(ProcessedEvent.of(messageId, eventType));
    }
}
