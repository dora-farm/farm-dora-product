package com.farmdora.farmdoraproduct.dto;

import java.util.List;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private int currentPage;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> contents;

    public PageResponseDto (List<T> contents, Page pages) {
        this.currentPage = pages.getNumber();
        this.pageSize = pages.getSize();
        this.totalElements = pages.getTotalElements();
        this.totalPages = pages.getTotalPages();
        this.hasNext = pages.hasNext();
        this.hasPrevious = pages.hasPrevious();
        this.contents = contents;
    }
}
