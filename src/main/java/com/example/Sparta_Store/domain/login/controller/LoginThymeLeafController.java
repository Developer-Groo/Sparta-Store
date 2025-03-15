package com.example.Sparta_Store.domain.login.controller;

import com.example.Sparta_Store.domain.oAuth.jwt.JwtUtil;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class LoginThymeLeafController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/users/login")
    public String login() {
        return "/users/login";
    }

    @GetMapping("/users/signUp")
    public String signUp() {
        return "/users/signUp";
    }

    @GetMapping("/users/main")
    public String mainPage() {
        return "/users/main";
    }

}
