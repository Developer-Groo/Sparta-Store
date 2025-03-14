package com.example.Sparta_Store.domain.likes.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikesErrorCode implements ErrorCode {
    NOT_EXISTS_USER(HttpStatus.NOT_FOUND, "NOT_FOUND", "유저가 존재하지 않습니다."),
    NOT_EXISTS_PRODUCT(HttpStatus.NOT_FOUND, "NOT_FOUND", "상품이 존재하지 않습니다."),
    PRODUCT_NOT_WISHLIST(HttpStatus.NOT_FOUND, "NOT_FOUND", "해당 상품을 찜하지 않았습니다."),
    PRODUCT_ALREADY_WISHLIST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "이미 찜한 상품 입니다.");


    private final HttpStatus status;
    private final String name;
    private final String message;
}
