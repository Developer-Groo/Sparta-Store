package com.example.Sparta_Store.user.service;

import com.example.Sparta_Store.address.entity.Address;
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
