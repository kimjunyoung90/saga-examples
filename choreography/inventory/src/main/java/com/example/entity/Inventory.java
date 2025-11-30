package com.example.entity;

import com.example.exception.InsufficientInventoryException;
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
    private Integer quantity;

    public void deduct(int quantity) {
        if(this.quantity < quantity) {
            throw new InsufficientInventoryException();
        }
        this.quantity -= quantity;
    }

    public void cancel(int quantity) {
        this.quantity += quantity;
    }
}