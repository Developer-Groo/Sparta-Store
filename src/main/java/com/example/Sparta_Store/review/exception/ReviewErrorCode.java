package com.example.Sparta_Store.review.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "리뷰를 수정할 권한이 없습니다."),
    INVALID_RATING_VALUE(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "별점은 1~5 사이의 정수여야 합니다."),
    REVIEW_NOT_PURCHASED(HttpStatus.FORBIDDEN, "FORBIDDEN", "구매한 상품만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_CONFIRMED(HttpStatus.FORBIDDEN, "FORBIDDEN", "구매 확정된 상품만 리뷰를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 리뷰는 존재하지 않습니다.");

    private final HttpStatus status;
    private final String name;
    private final String message;
}
