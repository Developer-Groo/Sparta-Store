package com.example.Sparta_Store.user;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.user.dto.request.UserRequestDto;
import com.example.Sparta_Store.user.dto.response.CreateUserResponseDto;

import com.example.Sparta_Store.user.exception.EmailAlreadyExistsException;
import com.example.Sparta_Store.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSignTest {
    @Mock
    private UserService userService;

    @Test
    @DisplayName("회원가입 - 성공케이스")
    void signUpTest(){
        // given
        Address address = new Address("","","");
        UserRequestDto requestDto = new UserRequestDto("test1@test.com","Test!1234","테스트용사용자1",address);

        CreateUserResponseDto responseDto = new CreateUserResponseDto("test1@test.com", "테스트용사용자1",address);
        when(userService.signUp(any(UserRequestDto.class))).thenReturn(responseDto);

        // when
        CreateUserResponseDto actualResult = userService.signUp(requestDto);

        // then
        assertThat(actualResult.email()).isEqualTo("test1@test.com");
        assertThat(actualResult.name()).isEqualTo("테스트용사용자1");
    }

    @Test
    @DisplayName("회원가입 - 실패케이스")
    void signUpFailureTest() {
        // given
        Address address = new Address("", "", "");
        UserRequestDto requestDto = new UserRequestDto("invalid-email", "short", "테스트용사용자1", address);

        when(userService.signUp(any(UserRequestDto.class))).thenThrow(new IllegalArgumentException("이메일형식이 올바르지않거나 비밀번호가 너무 짧습니다."));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(requestDto));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복으로인한 실패케이스")
    void signUpDuplicationEmail(){
        // given
        Address address = new Address("","","");
        UserRequestDto requestDto = new UserRequestDto("duplicate@test.com", "Test!1234", "테스트용사용자2", address);

        // when
        when(userService.signUp(any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("이메일이 이미 존재합니다."));

        // then
        assertThrows(EmailAlreadyExistsException.class, () -> userService.signUp(requestDto));
    }
}
