package com.example.Sparta_Store.user.service;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.save(user);
    }

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


    @Transactional
    public void updateUserPassword(Long userId, String oldPassword, String newPassword) {

        User user = userRepository.findByIdOrElseThrow(userId);

        //encode를 사용하는게 더 적절할 수 있다.
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 기존 비밀번호가 같습니다.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateUserInformation(Long userId, String userName, Address address) {
        User user = userRepository.findByIdOrElseThrow(userId);
        user.updateInformation(userName,address);
    }



}
