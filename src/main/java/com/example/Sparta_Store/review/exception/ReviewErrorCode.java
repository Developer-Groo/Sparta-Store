package com.example.Sparta_Store.review.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_PURCHASED(HttpStatus.FORBIDDEN, "FORBIDDEN", "구매한 상품만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_CONFIRMED(HttpStatus.FORBIDDEN, "FORBIDDEN", "구매 확정된 상품만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 리뷰는 존재하지 않습니다.");

    private final HttpStatus status;
    private final String name;
    private final String message;
}
