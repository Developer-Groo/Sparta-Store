package com.example.Sparta_Store.user.service.entity;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.users.service.UserRoleEnum;
import com.example.Sparta_Store.domain.users.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    private Users user;
    private Address address;

    @BeforeEach
    void setUp() {
        // 테스트용 주소 생성
        address = new Address("서울특별시", "테스트길", "12345");

        // 테스트용 사용자 생성
        user = new Users(
                "test@email.com",
                "encodedPassword",
                "테스트유저",
                address,
                UserRoleEnum.USER
        );
    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void testUpdateUserInfo() {
        // Given
        String newName = "변경된 유저";
        Address newAddress = new Address("부산광역시", "수정된길", "54321");

        // When
        user.updateUserInfo(newName, newAddress);

        // Then
        assertEquals(newName, user.getName());
        assertEquals(newAddress, user.getAddress());
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void testUpdatePassword() {
        // Given
        String newPassword = "newEncodedPassword";

        // When
        user.updatePassword(newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 (isDeleted 상태 변경)")
    void testDisableUser() {
        // When
        user.disableUser();

        // Then
        assertTrue(user.getIsDeleted());
    }
}

