package com.payment.idempotency;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProcessedEvent {

    @Id
    @Column(length = 36)
    private String messageId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    public static ProcessedEvent of(String messageId, String eventType) {
        return ProcessedEvent.builder()
                .messageId(messageId)
                .eventType(eventType)
                .processedAt(LocalDateTime.now())
                .build();
    }
}
