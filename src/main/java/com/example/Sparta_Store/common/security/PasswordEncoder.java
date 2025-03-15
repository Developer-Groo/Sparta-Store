package com.example.Sparta_Store.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}