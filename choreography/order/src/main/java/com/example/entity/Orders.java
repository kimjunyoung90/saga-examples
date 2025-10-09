package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long totalAmount;


}
