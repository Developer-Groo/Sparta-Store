package com.example.Sparta_Store.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String jwt = null;

        String authorizationHeader = request.getHeader("Authorization");

        // 회원가입 및 로그인시 토큰없어도 실행 가능
        if(requestURI.equals("/login") || requestURI.equals("/users/signUp")) {
            filterChain.doFilter(request,response);
            return;
        }

        // JWT 토큰 검증

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("JWT 토큰이 필요 합니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요 합니다.");
            return;
        }

        jwt = authorizationHeader.substring(7);

        // Secret Key 는 내가 만든게 맞는지 검증 만료 기간 지났는지 검증
        if (!jwtUtil.validateToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
        }

        if(requestURI.startsWith("/admin")) {

            // JWT에 관리자 권한이 있는지 확인
            if(jwtUtil.hasName(jwt,"ADMIN")) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            }
            return;
        }

        Long id = jwtUtil.extractId(jwt);
        request.setAttribute("id", id);

        filterChain.doFilter(request, response);
    }
}
