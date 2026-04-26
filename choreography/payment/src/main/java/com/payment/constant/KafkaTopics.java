package com.payment.constant;

public final class KafkaTopics {
    public static final String ORDER_EVENTS = "order-events";
    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String DLQ_SUFFIX = ".DLQ";

    private KafkaTopics() {
    }
}
