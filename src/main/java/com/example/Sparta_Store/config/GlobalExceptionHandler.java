package com.example.Sparta_Store.config;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        return ErrorResponse
                .toResponseEntity(ex.getErrorCode());
    }
}
