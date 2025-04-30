package com.farmdora.farmdoraproduct.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessMessage {
    REVISE_SUCCESS("수정 성공"),
    DELETE_SUCCESS("삭제 성공"),
    SEARCH_VIDEOS_SUCCESS("동영상 리스트 조회 성공"),
    SEARCH_SALES_SUCCESS("판매글 리스트 조회 성공"),
    REGISTER_PRODUCT_SUCCESS("상품 저장에 성공하였습니다."),
    REGISTER_VIDEO_SUCCESS("동영상 저장에 성공하였습니다.");

    private final String message;
}
