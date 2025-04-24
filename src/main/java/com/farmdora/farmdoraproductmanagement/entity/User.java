package com.farmdora.farmdoraproductmanagement.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "`user`")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 50)
    private String id;

    private String pwd;

    @Column(length = 40)
    private String name;

    @Column(length = 40)
    private String email;

    @Column(length = 50)
    private String accountNum;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "postNum", column = @Column(nullable = true)),
            @AttributeOverride(name = "addr", column = @Column(nullable = true)),
            @AttributeOverride(name = "detailAddr", column = @Column(nullable = true))
    })
    private Address address;

    private LocalDate birth;

    @Enumerated(EnumType.ORDINAL)
    private Gender sex;

    @Column(length = 30)
    private String phoneNum;

    @Column(nullable = false)
    private boolean isExpire;

    @Column(nullable = false)
    private boolean isBlind;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private BankType bankType;
}
