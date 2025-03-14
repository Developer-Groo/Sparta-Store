package com.example.Sparta_Store.domain.oAuth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    USER("ROLE_USER", "사용자 권한"),
    ADMIN("ROLE_ADMIN", "관리자 권한");

    private final String role;        // Spring Security 권한 이름
    private final String description; // 권한 설명
}