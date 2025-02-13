package com.example.Sparta_Store.cart.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements ErrorCode {
    NOT_EXISTS_USER(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 유저입니다.");



    private final HttpStatus status;
    private final String name;
    private final String message;
}
