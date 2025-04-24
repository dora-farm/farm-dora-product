package com.farmdora.farmdoraproductmanagement.common.exception;

import com.farmdora.farmdoraproductmanagement.common.response.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandler(CustomException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new HttpResponse(e.getStatus(), e.getMessage(), null));
    }
}
