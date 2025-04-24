package com.farmdora.farmdoraproductmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SaleIdsDto {
    private List<Integer> saleIds;
}
