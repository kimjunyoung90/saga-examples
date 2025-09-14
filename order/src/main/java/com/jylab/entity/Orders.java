package com.jylab.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @OneToMany(
        mappedBy = "orders", // 연관관계의 주인을 지정 order라는 필드를 가진 엔티티가 주인임을 표시
        cascade = CascadeType.ALL, // 삭제・저장 전파
        orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderItem> orderItems;
}
