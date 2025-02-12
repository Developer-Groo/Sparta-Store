package com.example.Sparta_Store.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    public static final String BEARER_PREFIX = "Bearer ";
    // JWT 토큰의 만료 시간 일단 60분으로 설정
    private final long TOKEN_TIME = 60 * 60 * 1000L;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(key) // 비밀 키를 사용하여 서명 검증
            .parseClaimsJws(token)
            .getBody();
    }

    public String generateToken(String email,String name,Long id) {
        Date date = new Date();

        if (id >= 1 && id <= 5) {
            name = "ADMIN"; // 관리자 이름으로 설정
        }

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(email) // 사용자 식별자 (ID)
                .claim("name" , name)
                .claim("id",id)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 설정
                .setIssuedAt(date) // 발급 시간 설정
                .signWith(key, signatureAlgorithm) // 비밀 키와 알고리즘으로 서명
                .compact(); // JWT 토큰 생성
    }

    public String extractRoles(String token) {
        return extractAllClaims(token).get("auth", String.class);
    }

    public Long extractId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public boolean hasRole(String token, String role) {
        String roles = extractRoles(token);
        return roles.contains(role);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true; // 토큰이 유효한 경우
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
        }
        return false; // 예외가 발생한 경우 토큰이 유효하지 않음
    }
}