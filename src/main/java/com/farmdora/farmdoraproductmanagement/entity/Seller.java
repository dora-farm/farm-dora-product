package com.farmdora.farmdoraproductmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "company_num", unique = true)
    private String companyNum;

    @Column(nullable = false, length = 50)
    private String name;

    @Embedded
    private Address address;

    @Column(nullable = false, length = 30)
    private String phoneNum;

    @Column(nullable = false)
    private String saveFile;

    @Column(nullable = false)
    private String originFile;

    @Column(nullable = false)
    private boolean isApprove;
}
