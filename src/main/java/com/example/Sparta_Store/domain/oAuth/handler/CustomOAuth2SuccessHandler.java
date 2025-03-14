package com.example.Sparta_Store.domain.oAuth.handler;


import com.example.Sparta_Store.domain.oAuth.jwt.JwtUtil;
import com.example.Sparta_Store.domain.user.service.UserRoleEnum;
import com.example.Sparta_Store.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 소셜 사용자 정보
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        UserRoleEnum role = userRepository.findByEmail(email).get().getRole();

        Long id = userRepository.findByEmail(email).get().getId();

        // JWT 생성
        String jwt = jwtUtil.generateToken(email,role,id);

        // 예시: response 헤더에 토큰 세팅
        response.setHeader("Authorization", jwt);

        super.setDefaultTargetUrl("/users/main");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
