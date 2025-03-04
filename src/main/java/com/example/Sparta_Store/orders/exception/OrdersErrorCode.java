package com.example.Sparta_Store.orders.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrdersErrorCode implements ErrorCode {
    NOT_EXISTS_USER(HttpStatus.NOT_FOUND, "NOT_FOUND", "유저가 존재하지 않습니다."),
    NOT_EXISTS_CART(HttpStatus.NOT_FOUND, "NOT_FOUND", "카트가 존재하지 않습니다."),
    NOT_EXISTS_CART_PRODUCT(HttpStatus.NOT_FOUND, "NOT_FOUND", "장바구니에 상품이 존재하지 않습니다."),
    NOT_EXISTS_ORDER(HttpStatus.NOT_FOUND, "NOT_FOUND", "주문 정보를 찾을 수 없습니다."),
    USER_MISMATCH(HttpStatus.FORBIDDEN, "FORBIDDEN", "주문자와 유저 정보가 일치하지 않습니다."),
    NOT_EXISTS_ORDER_ITEM(HttpStatus.NOT_FOUND, "NOT_FOUND", "주문 상품 정보를 찾을 수 없습니다."),
    ORDER_STATUS_CHANGE_FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "주문상태 변경 권한이 없습니다."),
    ORDER_STATUS_INVALID_TRANSITION(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "주문상태를 변경할 수 없습니다."),
    ORDER_COMPLETION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "주문완료 상태로 변경할 수 없습니다."),
    NOT_EXISTS_PAYMENT(HttpStatus.NOT_FOUND, "NOT_FOUND", "결제가 존재하지 않습니다.")
    ;


    private final HttpStatus status;
    private final String name;
    private final String message;

}
