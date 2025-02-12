package com.example.Sparta_Store.login.controller;

import com.example.Sparta_Store.login.dto.LoginRequest;
import com.example.Sparta_Store.login.dto.LoginResponse;
import com.example.Sparta_Store.login.service.LoginService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        String token  = loginService.login(request);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
