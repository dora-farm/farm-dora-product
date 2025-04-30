package com.farmdora.farmdoraproduct.dto;

import com.farmdora.farmdoraproduct.entity.Broadcast;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastSearchDto {
    private String keyword;
    private String sort;
    private int page;
    private int size;
}
