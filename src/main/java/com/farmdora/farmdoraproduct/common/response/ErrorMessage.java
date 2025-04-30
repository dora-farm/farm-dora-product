package com.farmdora.farmdoraproduct.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    ORDER_NOT_FOUND("존재하지 않는 주문입니다."),
    UPDATE_FAIL("변경 실패"),
    DELETE_FAIL("삭제 실패");

    private final String message;
}