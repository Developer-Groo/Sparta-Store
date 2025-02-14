package com.example.Sparta_Store.cart.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements ErrorCode {
    NOT_EXISTS_USER(HttpStatus.NOT_FOUND, "NOT_FOUND", "유저가 존재하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "해당 상품을 찾을 수 없습니다."),
    NOT_EXISTS_CART_PRODUCT(HttpStatus.NOT_FOUND, "NOT_FOUND", "장바구니에 상품이 존재하지 않습니다."),
    PRODUCT_QUANTITY_TOO_LOW(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "상품 수량은 1 이상이어야 합니다.");



    private final HttpStatus status;
    private final String name;
    private final String message;
}
