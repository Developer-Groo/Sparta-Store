package com.example.Sparta_Store.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();
    String getName();
    String getMessage();
}
