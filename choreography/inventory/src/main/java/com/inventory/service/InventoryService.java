package com.inventory.service;

import com.inventory.constant.KafkaTopics;
import com.inventory.dto.InventoryRequest;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryReservation;
import com.inventory.exception.InventoryNotFoundException;
import com.inventory.outbox.OutboxService;
import com.inventory.producer.event.InventoryConfirmed;
import com.inventory.producer.event.InventoryCreated;
import com.inventory.producer.event.InventoryMessage;
import com.inventory.producer.event.MessageType;
import com.inventory.repository.InventoryRepository;
import com.inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final OutboxService outboxService;

    @Transactional
    public ReserveResult reserve(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(InventoryNotFoundException::new);

        if (!inventory.canReserve(request.quantity())) {
            return ReserveResult.insufficient();
        }

        inventory.reserve(request.quantity());

        InventoryReservation reservation = InventoryReservation.reserve(
                request.orderId(),
                request.productId(),
                request.quantity()
        );
        reservationRepository.save(reservation);

        InventoryCreated payload = InventoryCreated.builder()
                .orderId(request.orderId())
                .inventoryId(inventory.getId())
                .productId(inventory.getProductId())
                .status("SUCCESS")
                .build();

        InventoryMessage envelope = InventoryMessage.builder()
                .type(MessageType.INVENTORY_RESERVED.name())
                .payload(payload)
                .build();

        outboxService.record(
                KafkaTopics.INVENTORY_EVENTS,
                MessageType.INVENTORY_RESERVED.name(),
                String.valueOf(request.orderId()),
                envelope
        );

        return ReserveResult.success(inventory);
    }

    @Transactional
    public void confirm(Long orderId) {
        InventoryReservation reservation = reservationRepository.findByOrderId(orderId)
                .orElse(null);

        if (reservation == null) {
            log.warn("No reservation found for confirm: orderId={} (likely INVENTORY_FAILED earlier)", orderId);
            return;
        }
        if (!reservation.isReserved()) {
            log.info("Reservation already in status={} for orderId={}, skip confirm", reservation.getStatus(), orderId);
            return;
        }

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(InventoryNotFoundException::new);

        inventory.confirmReservation(reservation.getQuantity());
        reservation.confirm();

        InventoryConfirmed payload = InventoryConfirmed.builder()
                .orderId(reservation.getOrderId())
                .productId(reservation.getProductId())
                .quantity(reservation.getQuantity())
                .build();

        InventoryMessage envelope = InventoryMessage.builder()
                .type(MessageType.INVENTORY_CONFIRMED.name())
                .payload(payload)
                .build();

        outboxService.record(
                KafkaTopics.INVENTORY_EVENTS,
                MessageType.INVENTORY_CONFIRMED.name(),
                String.valueOf(orderId),
                envelope
        );
    }

    @Transactional
    public void cancelByOrderId(Long orderId) {
        InventoryReservation reservation = reservationRepository.findByOrderId(orderId)
                .orElse(null);

        if (reservation == null) {
            log.warn("No reservation found for cancel: orderId={} (likely INVENTORY_FAILED earlier)", orderId);
            return;
        }
        if (!reservation.isReserved()) {
            log.info("Reservation already in status={} for orderId={}, skip cancel", reservation.getStatus(), orderId);
            return;
        }

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(InventoryNotFoundException::new);

        inventory.cancelReservation(reservation.getQuantity());
        reservation.cancel();
    }
}
