package com.example.Sparta_Store.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // Exception 메시지 설정
        this.errorCode = errorCode;
    }
}
