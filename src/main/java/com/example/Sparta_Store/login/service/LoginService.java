package com.example.Sparta_Store.login.service;

import com.example.Sparta_Store.config.jwt.JwtUtil;
import com.example.Sparta_Store.config.PasswordEncoder;
import com.example.Sparta_Store.login.dto.LoginRequest;
import com.example.Sparta_Store.login.repository.LoginRepository;
import com.example.Sparta_Store.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;

    public String login(LoginRequest request) {

        String email = request.email();
        String password = request.password();

        User user = loginRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getEmail(),user.getName(), user.getId());
    }
}