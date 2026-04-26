package com.inventory.producer.event;

public enum MessageType {
    INVENTORY_RESERVED,
    INVENTORY_CONFIRMED,
    INVENTORY_FAILED,
    INVENTORY_CANCELED,
}
