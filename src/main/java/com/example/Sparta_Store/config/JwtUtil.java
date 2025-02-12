package com.example.Sparta_Store.config;

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
    // JWT 토큰의 접두사
    public static final String BEARER_PREFIX = "Bearer ";
    // JWT 토큰의 만료 시간 일단 60분으로 설정
    private final long TOKEN_TIME = 60 * 60 * 1000L;
    // JWT 서명 알고리즘
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    // 애플리케이션 설정 파일에서 주입받은 비밀 키
    @Value("${jwt.secret.key}")
    private String secretKey;
    // 실제 서명에 사용되는 키 객체
    private Key key;

    /**
     * 빈 초기화 메서드
     * - 애플리케이션 시작 시 비밀 키를 Base64로 디코딩하여 Key 객체를 초기화합니다.
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출합니다.
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }



    /**
     * JWT 토큰에서 모든 클레임을 추출합니다.
     * @param token JWT 토큰
     * @return 클레임 객체
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(key) // 비밀 키를 사용하여 서명 검증
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * JWT 토큰을 생성합니다.
     * @param email 사용자 이름
     * @return 생성된 JWT 토큰
     */
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

    /**
     * JWT 토큰에서 역할(권한) 정보를 추출합니다.
     * @param token JWT 토큰
     * @return 역할 정보 (문자열)
     */
    public String extractRoles(String token) {
        return extractAllClaims(token).get("auth", String.class);
    }

    /**
     * JWT 토큰에서 특정 역할이 포함되어 있는지 확인합니다.
     * @param token JWT 토큰
     * @param role 확인할 역할
     * @return 역할 포함 여부 (true: 포함됨, false: 포함되지 않음)
     */
    public boolean hasRole(String token, String role) {
        String roles = extractRoles(token);
        return roles.contains(role);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰의 유효성 여부 (true: 유효함, false: 유효하지 않음)
     */
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