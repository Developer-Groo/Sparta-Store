package com.example.Sparta_Store.init.users;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.users.entity.Users;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
import com.example.Sparta_Store.domain.users.service.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersInitService {

    private final UserRepository userRepository;

    @Transactional
    public void initialize() {
        Address address = new Address("", "", "");

        if (userRepository.existsByEmail("admin")) return;
        Users user = new Users("admin", "admin", "admin", address, UserRoleEnum.ADMIN);

        userRepository.save(user);
    }
}
