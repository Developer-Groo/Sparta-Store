package com.example.Sparta_Store.domain.coupon.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    EXISTS_COUPON_USER(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "이미 발급된 쿠폰입니다."),
    COUPON_EXHAUSTED(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "쿠폰이 모두 소진되었습니다."),
    COUPON_BUSY(HttpStatus.FORBIDDEN, "FORBIDDEN", "락 획득에 실패했습니다."),
    TRY_AGAIN_LATER(HttpStatus.FORBIDDEN, "FORBIDDEN", "잠시후에 다시 시도하세요.")
    ;

    private final HttpStatus status;
    private final String name;
    private final String message;
}
