package com.inventory.entity;

import com.inventory.exception.InsufficientInventoryException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    public boolean canReserve(int quantity) {
        return this.availableQuantity >= quantity;
    }

    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new InsufficientInventoryException();
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    public void confirmReservation(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot confirm more than reserved");
        }
        this.reservedQuantity -= quantity;
    }

    public void cancelReservation(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot cancel more than reserved");
        }
        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
    }
}
