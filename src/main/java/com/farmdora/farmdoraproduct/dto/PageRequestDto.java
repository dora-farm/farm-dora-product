package com.farmdora.farmdoraproduct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class PageRequestDto {
    private Integer page;

    public Pageable toPageable() {
        final int size = 15;
        if (this.page != null) {
            return PageRequest.of(this.page, size);
        }
        return PageRequest.of(0, size);
    }
}