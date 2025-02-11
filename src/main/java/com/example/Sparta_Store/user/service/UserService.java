package com.example.Sparta_Store.user.service;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.config.PasswordEncoder;
import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserResponseDto signUp(String email, String password, String name, Address address){

        String encodePassword = passwordEncoder.encode(password);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일 입니다.");
        }

        User user = new User( email, encodePassword, name, address);

        User saveUser = userRepository.save(user);

        return CreateUserResponseDto.createDto(saveUser);

    }
}
