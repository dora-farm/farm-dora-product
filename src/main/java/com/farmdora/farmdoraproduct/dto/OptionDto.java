package com.farmdora.farmdoraproduct.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {
    private String name;
    private int price;
    private int quantity;
}
