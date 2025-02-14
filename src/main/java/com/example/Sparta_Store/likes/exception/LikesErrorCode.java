package com.example.Sparta_Store.likes.exception;

import com.example.Sparta_Store.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikesErrorCode implements ErrorCode {
    ;


    private final HttpStatus status;
    private final String name;
    private final String message;
}
