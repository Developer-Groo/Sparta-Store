package com.example.Sparta_Store.coupon.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    EXISTS_COUPON_USER(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "이미 발급된 쿠폰입니다."),
    COUPON_EXHAUSTED(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "쿠폰이 모두 소진되었습니다.")
    ;

    private final HttpStatus status;
    private final String name;
    private final String message;
}
