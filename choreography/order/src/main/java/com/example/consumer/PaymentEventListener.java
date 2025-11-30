package com.example.consumer;

import com.example.entity.Order;
import com.example.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Event;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private OrderService orderService;

    public void handlePaymentEvent(ConsumerRecord<String, String> record) throws JsonProcessingException {
        EventMessage eventMessage = objectMapper.readValue(record.value(), EventMessage.class);
        switch (eventMessage.type()) {
            case PAYMENT_FAILED -> handlePaymentFailedEvent(eventMessage.payload());
        }

    }

    private void handlePaymentFailedEvent(JsonNode payload) throws JsonProcessingException{
        PaymentFailed paymentFailed = objectMapper.readValue(payload.toString(), PaymentFailed.class);
        orderService.cancelOrder(paymentFailed.orderId());
    }
}
