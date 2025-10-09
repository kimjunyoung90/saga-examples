package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private int orderId;

    @Column
    private int amount;

}
