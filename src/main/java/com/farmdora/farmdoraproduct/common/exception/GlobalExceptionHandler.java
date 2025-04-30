package com.farmdora.farmdoraproduct.common.exception;

import com.farmdora.farmdoraproduct.common.response.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ILLEGAL_ERROR_MESSAGE = "서버 에러가 발생했습니다.";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> customExceptionHandler(BaseException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new HttpResponse(e.getStatus(), e.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> customExceptionHandler(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ILLEGAL_ERROR_MESSAGE, null));
    }
}
