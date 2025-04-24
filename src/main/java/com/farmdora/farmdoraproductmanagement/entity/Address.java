package com.farmdora.farmdoraproductmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
@Embeddable
public class Address {

    @Column(nullable = false)
    private String addr;

    @Column(nullable = false)
    private String detailAddr;

    @Column(nullable = false, length = 5)
    private String postNum;
}
