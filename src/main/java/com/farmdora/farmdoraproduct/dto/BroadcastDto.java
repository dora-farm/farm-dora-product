package com.farmdora.farmdoraproduct.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastDto {
    private Integer sellerId;
    private String title;
    private String content;
}
