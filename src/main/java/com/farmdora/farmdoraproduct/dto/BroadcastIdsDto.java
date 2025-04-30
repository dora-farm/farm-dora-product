package com.farmdora.farmdoraproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BroadcastIdsDto {
    private List<Integer> broadcastIds;
}
