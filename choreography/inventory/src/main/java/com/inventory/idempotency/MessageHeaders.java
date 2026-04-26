package com.inventory.idempotency;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;

public final class MessageHeaders {

    public static final String MESSAGE_ID = "messageId";

    private MessageHeaders() {
    }

    public static String extractMessageId(ConsumerRecord<?, ?> record) {
        Header header = record.headers().lastHeader(MESSAGE_ID);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
