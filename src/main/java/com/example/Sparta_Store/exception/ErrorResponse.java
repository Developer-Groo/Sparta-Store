package com.example.Sparta_Store.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private int status;
    private String name;
    private String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                        ErrorResponse.builder()
                                .status(errorCode.getStatus().value())
                                .name(errorCode.getName())
                                .message(errorCode.getMessage())
                                .build()
                );
    }
}
