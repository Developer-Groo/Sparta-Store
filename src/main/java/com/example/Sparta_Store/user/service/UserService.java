package com.example.Sparta_Store.user.service;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.config.PasswordEncoder;
import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.dto.UserResponseDto;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponseDto signUp(
            String email,
            String password,
            String name,
            Address address
    ) {
        String encodePassword = passwordEncoder.encode(password);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일 입니다.");
        }

        User user = new User(
                email,
                encodePassword,
                name,
                address
        );

        User saveUser = userRepository.save(user);

        return CreateUserResponseDto.createDto(saveUser);

    }

    // 회원 정보 수정 (이름, 주소만 변경)
    @Transactional
    public UserResponseDto updateInfo(
            Long userId,
            String name,
            Address address
    ) { // 유저 아이디, 네임 , 주소를 인자로 받음

        User user = userRepository.findById(userId) // 아이디를 조회
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 없으면 예외처리

        user.updateUserInfo(name, address); // 있으면 이름과 주소 변경

        return toUpdateInfoDto();
    }

    // 비밀번호 변경 (현재 비밀번호 검증 후 변경)
    @Transactional
    public UserResponseDto updatePassword(
            Long userId,
            String oldPassword,
            String newPassword
    ) { // 유저 아이디 , 현재 비밀번호 , 바뀔 비밀번호

        User user = userRepository.findById(userId) // 바꿀 아이디 조회
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 아이디가 없는 경우 예외처리

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) { //현재 비밀번호와 지금 비밀번호가 다를 경우
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다."); // 예외 발생
        }

        user.updatePassword(passwordEncoder.encode(newPassword)); // 일치하면 새 비밀번호로 바꾸고 암호화까지 진행

        return toUpdatePasswordDto();
    }

    // 회원 탈퇴 (비밀번호 검증 후 isDeleted 변경)
    @Transactional
    public UserResponseDto deleteUser(Long userId, String rawPassword) { // 유저 아이디, 현재 비밀번호
        User user = userRepository.findById(userId) // 삭제할 아이디 조회
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 예외처리

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) { // 삭제할 아이디에대한 비밀번호가 불일치 할 경우
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다."); // 예외발생w
        }

        user.disableUser(); // 삭제

        return toDeleteUserDto();
    }

    private UserResponseDto toUpdateInfoDto() {
        return new UserResponseDto("회원 정보 수정이 완료되었습니다.");
    }

    private UserResponseDto toUpdatePasswordDto() {
        return new UserResponseDto("비밀번호 변경이 완료되었습니다.");
    }

    private UserResponseDto toDeleteUserDto() {
        return new UserResponseDto("회원 탈퇴가 완료되었습니다.");
    }

}
