package com.example.Sparta_Store.exception.item;

import com.example.Sparta_Store.exception.global.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ItemErrorCode implements ErrorCode {
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "재고가 부족합니다.");

    private final HttpStatus status;
    private final String name;
    private final String message;
}
