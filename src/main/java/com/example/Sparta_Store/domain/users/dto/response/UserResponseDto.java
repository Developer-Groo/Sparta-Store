package com.example.Sparta_Store.domain.users.dto.response;

public record UserResponseDto(String message) {

    // 회원 정보 수정 성공 메세지 반환
    public static UserResponseDto updateInfoSuccess() {
        return new UserResponseDto("회원 정보 수정이 완료되었습니다.");
    }

    // 비밀번호 변경 성공 메시지 반환
    public static UserResponseDto updatePasswordSuccess() {
        return new UserResponseDto("비밀번호 변경이 완료되었습니다.");
    }

    // 회원 탈퇴 성공 메시지 반환
    public static UserResponseDto deleteUserSuccess() {
        return new UserResponseDto("회원 탈퇴가 완료되었습니다.");
    }
}
