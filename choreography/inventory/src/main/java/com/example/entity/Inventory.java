package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private int code;

    @Column
    private int quantity;
}
