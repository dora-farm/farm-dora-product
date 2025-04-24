package com.farmdora.farmdoraproductmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sale extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "type_id")
    private SaleType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 50)
    private String origin;

    private boolean isBlind;
}
