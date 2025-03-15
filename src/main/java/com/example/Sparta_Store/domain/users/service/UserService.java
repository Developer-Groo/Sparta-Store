package com.example.Sparta_Store.domain.users.service;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.address.entity.AddressDto;
import com.example.Sparta_Store.common.security.PasswordEncoder;
import com.example.Sparta_Store.domain.users.dto.response.CreateUserResponseDto;
import com.example.Sparta_Store.domain.users.dto.response.UserResponseDto;
import com.example.Sparta_Store.domain.users.entity.Users;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
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

        Users user = new Users(
                email,
                encodePassword,
                name,
                address,
                UserRoleEnum.USER
        );

        return CreateUserResponseDto.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDto updateInfo(
            Long userId,
            String name,
            AddressDto address
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Address newAddress = (address != null)
                ? new Address(address.city(), address.street(), address.zipcode())
                : null;

        user.updateUserInfo(name, newAddress != null ? newAddress : user.getAddress());

        return UserResponseDto.updateInfoSuccess();
    }

    @Transactional
    public UserResponseDto updatePassword(
            Long userId,
            String oldPassword,
            String newPassword
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));

        return UserResponseDto.updatePasswordSuccess();
    }

    @Transactional
    public UserResponseDto deleteUser(Long userId, String rawPassword) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.disableUser();

        return UserResponseDto.deleteUserSuccess();
    }

}
