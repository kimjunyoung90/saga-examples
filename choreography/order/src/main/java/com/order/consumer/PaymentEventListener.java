package com.order.consumer;

import com.order.constant.KafkaTopics;
import com.order.consumer.event.EventMessage;
import com.order.consumer.event.PaymentApproved;
import com.order.consumer.event.PaymentFailed;
import com.order.idempotency.IdempotencyService;
import com.order.idempotency.MessageHeaders;
import com.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "order-service"
    )
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String messageId = MessageHeaders.extractMessageId(record);
        if (idempotencyService.isDuplicate(messageId)) {
            log.info("Skip duplicate payment event messageId={}", messageId);
            return;
        }

        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case PAYMENT_APPROVED -> handlePaymentApprovedEvent(eventMessage.payload());
            case PAYMENT_FAILED -> handlePaymentFailedEvent(eventMessage.payload());
        }

        idempotencyService.markProcessed(messageId, eventMessage.type().name());
    }

    private void handlePaymentApprovedEvent(JsonNode payload) throws JsonProcessingException {
        PaymentApproved paymentApproved = objectMapper.readValue(payload.toString(), PaymentApproved.class);
        orderService.approveOrder(paymentApproved.orderId());
    }

    private void handlePaymentFailedEvent(JsonNode payload) throws JsonProcessingException {
        PaymentFailed paymentFailed = objectMapper.readValue(payload.toString(), PaymentFailed.class);
        orderService.cancelOrder(paymentFailed.orderId());
    }
}
