package com.example.Sparta_Store.exception.global;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    String getName();

    String getMessage();
}
