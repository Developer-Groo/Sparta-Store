package com.example.Sparta_Store.oAuth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String email = null;
        String jwt = null;

        // 회원가입 및 로그인시 토큰없어도 실행 가능

         if( requestURI.equals("/login") ||
             requestURI.equals("/users/signUp") ||
             requestURI.equals("/users/login") ||
             requestURI.equals("/users/main")
         ) {
            filterChain.doFilter(request,response);
            return;
        }

        if(requestURI.contains("items") && request.getMethod().equals("GET")) {
            filterChain.doFilter(request,response);
            return;
        }

        if (requestURI.endsWith(".css")) {
            // CSS 파일 요청인 경우 필터 처리를 건너뜁니다.
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

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

        request.setAttribute("id",jwtUtil.extractId(jwt));

        email = jwtUtil.extractEmail(jwt);

        String auth = jwtUtil.extractRole(jwt);
        UserRoleEnum userRole = UserRoleEnum.valueOf(auth);
        User user = new User(email,"", List.of(userRole::getRole));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}