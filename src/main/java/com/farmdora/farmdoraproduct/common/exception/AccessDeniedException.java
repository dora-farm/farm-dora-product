package com.farmdora.farmdoraproduct.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends BaseException {
    public AccessDeniedException() {
        super("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
}
