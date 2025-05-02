package com.farmdora.farmdoraproduct.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastSaleDto {
    // Sale 정보
    private int id;
    private String title;
    // option의 정보 (최초 1개만)
    private String name;
    private int price;
    // sale_file의 is_main이 false인 파일 1개 추출
    private String mainImage;
}
