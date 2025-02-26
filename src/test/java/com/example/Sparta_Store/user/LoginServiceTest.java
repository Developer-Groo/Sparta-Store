package com.example.Sparta_Store.user;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.config.PasswordEncoder;
import com.example.Sparta_Store.login.dto.LoginRequest;
import com.example.Sparta_Store.login.repository.LoginRepository;
import com.example.Sparta_Store.login.service.LoginService;
import com.example.Sparta_Store.oAuth.jwt.JwtUtil;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.user.entity.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("로그인 - 성공케이스")
    void loginSuccessTest() {
        // given
        Address address = new Address("","","");
        String email = "test1@test.com";
        String password = "Test!1234";
        LoginRequest request = new LoginRequest(email, password);

        Users user = new Users(email, passwordEncoder.encode(password), "USER", address);
        String expectedToken = "생성된 토큰";

        when(loginRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email, user.getRole(), user.getId())).thenReturn(expectedToken);

        // when
        String actualToken = loginService.login(request);

        // then
        assertThat(actualToken).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("로그인 - 실패케이스 (등록된 사용자가 없음)")
    void loginUserNotFoundTest() {
        // given
        String email = "nonexistent@test.com";
        String password = "Test!1234";
        LoginRequest request = new LoginRequest(email, password);

        when(loginRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> loginService.login(request));
    }

    @Test
    @DisplayName("로그인 - 실패케이스 (탈퇴한 회원)")
    void loginDeletedUserTest() {
        // given
        Address address = new Address("","","");
        String email = "deleted@test.com";
        String password = "Test!1234";
        LoginRequest request = new LoginRequest(email, password);

        Users user = new Users(1L,"","USER",email, passwordEncoder.encode(password), address, true, "", "", UserRoleEnum.USER);

        when(loginRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> loginService.login(request));
    }

    @Test
    @DisplayName("로그인 - 실패케이스 (비밀번호 불일치)")
    void loginPasswordMismatchTest() {
        // given
        Address address = new Address("","","");
        String email = "test1@test.com";
        String password = "wrongPassword";
        LoginRequest request = new LoginRequest(email, password);

        Users user = new Users(email, passwordEncoder.encode("Test!1234"), "USER" ,address);

        when(loginRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> loginService.login(request));
    }
}