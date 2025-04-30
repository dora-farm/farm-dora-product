package com.farmdora.farmdoraproduct.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class PageRequestDto {
    private Integer page;
    private Integer size;

    public Pageable toPageable() {
        final int defaultSize = 15;
        // size가 null이면 기본값 사용, 아니면 전달된 값 사용
        int pageSize = (this.size != null) ? this.size : defaultSize;

        // page가 null이면 0페이지, 아니면 전달된
        int pageNumber = (this.page != null) ? this.page : 0;

        return PageRequest.of(pageNumber, pageSize);
    }
}