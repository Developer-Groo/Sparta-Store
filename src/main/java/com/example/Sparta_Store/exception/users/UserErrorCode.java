package com.example.Sparta_Store.exception.users;

import com.example.Sparta_Store.exception.global.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    ;

    private final HttpStatus status;
    private final String name;
    private final String message;
}
