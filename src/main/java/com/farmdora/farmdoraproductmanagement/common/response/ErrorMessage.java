package com.farmdora.farmdoraproductmanagement.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    ORDER_NOT_FOUND("존재하지 않는 주문입니다.");

    private final String message;
}