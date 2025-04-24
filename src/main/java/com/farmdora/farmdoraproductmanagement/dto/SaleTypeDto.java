package com.farmdora.farmdoraproductmanagement.dto;

import com.farmdora.farmdoraproductmanagement.entity.SaleType;
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
public class SaleTypeDto {
    // SaleType 정보
    private Short typeId;
    private String typeName;

    // SaleTypeBig 정보
    private Short typeBigId;
    private String typeBigName;

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static SaleTypeDto from(SaleType saleType) {
        if (saleType == null) {
            return null;
        }

        SaleTypeDto dto = new SaleTypeDto();

        // SaleType 정보 설정
        dto.setTypeId(saleType.getId());
        dto.setTypeName(saleType.getName());

        // SaleTypeBig 정보 설정
        if (saleType.getSaleTypeBig() != null) {
            dto.setTypeBigId(saleType.getSaleTypeBig().getId());
            dto.setTypeBigName(saleType.getSaleTypeBig().getName());
        }

        return dto;
    }

    // 생성자를 이용한 변환 (대안 방법)
    public SaleTypeDto(SaleType saleType) {
        if (saleType != null) {
            this.typeId = saleType.getId();
            this.typeName = saleType.getName();

            if (saleType.getSaleTypeBig() != null) {
                this.typeBigId = saleType.getSaleTypeBig().getId();
                this.typeBigName = saleType.getSaleTypeBig().getName();
            }
        }
    }
}