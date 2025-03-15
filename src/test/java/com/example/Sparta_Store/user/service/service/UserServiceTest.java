package com.example.Sparta_Store.user.service.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.address.entity.AddressDto;
import com.example.Sparta_Store.common.security.PasswordEncoder;
import com.example.Sparta_Store.domain.users.service.UserRoleEnum;
import com.example.Sparta_Store.domain.users.dto.response.CreateUserResponseDto;
import com.example.Sparta_Store.domain.users.dto.response.UserResponseDto;
import com.example.Sparta_Store.domain.users.entity.Users;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
import com.example.Sparta_Store.domain.users.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users user;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("서울특별시", "테스트길", "12345");
        user = new Users("test@email.com", "encodedPassword", "테스트유저", address, UserRoleEnum.USER);
    }

    // 회원 가입 테스트
    @Test
    @DisplayName("회원 가입 테스트 - 성공")
    void testSignUp_Success() {
        // Given
        String email = "newuser@email.com";
        String password = "Test123!";
        String encodedPassword = "encodedPassword";
        String name = "새로운 유저";

        Address address = new Address("서울특별시2", "새로운길2", "54321");

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // When
        CreateUserResponseDto response = userService.signUp(email, password, name, address);

        // Then
        assertEquals(email, response.email());
        assertEquals(name, response.name());
        assertEquals(address, response.address());

        then(userRepository).should(times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("회원 가입 테스트 - 실패 (이미 존재하는 이메일)")
    void testSignUp_Fail_DuplicateEmail() {
        // Given
        String email = "test@email.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // When & Then
        assertThrows(RuntimeException.class, () ->
                userService.signUp(email, "Test123!", "테스트유저", address));

        then(userRepository).should(never()).save(any(Users.class));
    }

    @Test
    @DisplayName("회원 정보 수정 테스트 - 성공")
    void testUpdateUserInfo_Success() {

        // Given
        Long userId = 1L;
        String newName = "수정된 유저";
        AddressDto newAddressDto = new AddressDto("부산광역시", "수정된길", "67890");
        Address newAddress = new Address(newAddressDto.city(), newAddressDto.street(), newAddressDto.zipcode());


        given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));

        // When
        UserResponseDto response = userService.updateInfo(userId, newName, newAddressDto);

        // Then
        assertEquals("회원 정보 수정이 완료되었습니다.", response.message());
        assertEquals(newName, user.getName());


        then(userRepository).should(times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트 - 실패 (회원 없음)")
    void testUpdateUserInfo_Fail_UserNotFound() {
        // Given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateInfo(userId, "새로운이름", new AddressDto("부산", "길", "56789")));

        then(userRepository).should(times(1)).findById(userId);
    }

    // 비밀번호 변경 테스트
    @Test
    @DisplayName("비밀번호 변경 테스트 - 성공")
    void testUpdatePassword_Success() {
        // Given
        Long userId = 1L;
        String oldPassword = "Test123!";
        String newPassword = "NewPass123!";
        String encodedNewPassword = "encodedNewPassword";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

        // When
        UserResponseDto response = userService.updatePassword(userId, oldPassword, newPassword);

        // Then
        assertEquals("비밀번호 변경이 완료되었습니다.", response.message());
        assertEquals(encodedNewPassword, user.getPassword());

        then(userRepository).should(times(1)).findById(userId);
    }

    @Test
    @DisplayName("비밀번호 변경 테스트 - 실패 (현재 비밀번호 불일치)")
    void testUpdatePassword_Fail_WrongOldPassword() {
        // Given
        Long userId = 1L;
        String oldPassword = "WrongPass!";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                userService.updatePassword(userId, oldPassword, "NewPass123!"));

        then(userRepository).should(times(1)).findById(userId);
    }

    // 회원 탈퇴 테스트
    @Test
    @DisplayName("회원 탈퇴 테스트 - 성공")
    void testDeleteUser_Success() {
        // Given
        Long userId = 1L;
        String password = "Test123!";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);

        // When
        UserResponseDto response = userService.deleteUser(userId, password);

        // Then
        assertEquals("회원 탈퇴가 완료되었습니다.", response.message());
        assertTrue(user.getIsDeleted());

        then(userRepository).should(times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 - 실패 (비밀번호 불일치)")
    void testDeleteUser_Fail_WrongPassword() {
        // Given
        Long userId = 1L;
        String wrongPassword = "WrongPass!";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(wrongPassword, user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(userId, wrongPassword));

        then(userRepository).should(times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 - 실패 (회원 없음)")
    void testDeleteUser_Fail_UserNotFound() {
        // Given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(userId, "Test123!"));

        then(userRepository).should(times(1)).findById(userId);
    }
}

