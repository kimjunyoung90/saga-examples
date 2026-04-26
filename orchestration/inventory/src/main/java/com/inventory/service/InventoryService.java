package com.inventory.service;

import com.inventory.dto.InventoryRequest;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryReservation;
import com.inventory.exception.InventoryNotFoundException;
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

    @Transactional
    public Inventory reserve(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
                .orElseThrow(InventoryNotFoundException::new);

        inventory.reserve(request.quantity());

        InventoryReservation reservation = InventoryReservation.reserve(
                request.orderId(),
                request.productId(),
                request.quantity()
        );
        reservationRepository.save(reservation);

        return inventory;
    }

    @Transactional
    public Inventory confirm(Long orderId) {
        InventoryReservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Reservation not found: orderId=" + orderId));

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(InventoryNotFoundException::new);

        if (!reservation.isReserved()) {
            log.info("Reservation already in status={} for orderId={}, skip confirm", reservation.getStatus(), orderId);
            return inventory;
        }

        inventory.confirmReservation(reservation.getQuantity());
        reservation.confirm();

        return inventory;
    }

    @Transactional
    public Inventory cancelByOrderId(Long orderId) {
        InventoryReservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Reservation not found: orderId=" + orderId));

        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(InventoryNotFoundException::new);

        if (!reservation.isReserved()) {
            log.info("Reservation already in status={} for orderId={}, skip cancel", reservation.getStatus(), orderId);
            return inventory;
        }

        inventory.cancelReservation(reservation.getQuantity());
        reservation.cancel();

        return inventory;
    }
}
