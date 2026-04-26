package com.inventory.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_messages", indexes = {
        @Index(name = "idx_outbox_status_created_at", columnList = "status, createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OutboxMessage {

    private static final int MAX_ERROR_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String messageId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String eventType;

    @Column(length = 500)
    private String messageKey;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private int retryCount;

    private LocalDateTime lastAttemptAt;

    @Column(length = MAX_ERROR_LENGTH)
    private String lastError;

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void recordFailure(String error) {
        this.retryCount++;
        this.lastAttemptAt = LocalDateTime.now();
        this.lastError = truncate(error);
    }

    public void markFailed() {
        this.status = OutboxStatus.FAILED;
        this.lastAttemptAt = LocalDateTime.now();
    }

    private static String truncate(String error) {
        if (error == null) {
            return null;
        }
        return error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH);
    }

    public enum OutboxStatus {
        PENDING,
        PUBLISHED,
        FAILED
    }
}
