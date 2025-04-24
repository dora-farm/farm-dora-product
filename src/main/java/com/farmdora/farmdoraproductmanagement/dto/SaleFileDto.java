package com.farmdora.farmdoraproductmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleFileDto {
    private String saveFile;
    private String originFile;
    private boolean isMain;
}