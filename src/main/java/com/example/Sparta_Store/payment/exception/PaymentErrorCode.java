package com.example.Sparta_Store.payment.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    NOT_EXISTS_ORDER(HttpStatus.NOT_FOUND, "NOT_FOUND", "주문 정보를 찾을 수 없습니다."),
    MUST_BE_BEFORE_PAYMENT(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "주문 상태가 'BEFORE_PAYMENT' 이어야 합니다."),
    USER_MISMATCH(HttpStatus.FORBIDDEN, "FORBIDDEN", "주문자와 유저 정보가 일치하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "결제 금액이 변동되었습니다."),
    NOT_EXISTS_PAYMENT(HttpStatus.NOT_FOUND, "NOT_FOUND", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_CANCELLATION_FAILED(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "결제 승인 취소에 실패했습니다."),
    ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "이미 취소된 결제입니다.")
    ;

    private final HttpStatus status;
    private final String name;
    private final String message;

}
