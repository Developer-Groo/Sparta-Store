package com.example.Sparta_Store.user.dto;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.user.entity.User;

public record CreateUserResponseDto(
    String email,
    String password,
    String name,
    Address address,
    Long id
) {
    public static CreateUserResponseDto createDto(User user) {
        return new CreateUserResponseDto(
            user.getEmail(),
            user.getPassword(),
            user.getName(),
            user.getAddress(),
            user.getId()
        );
    }
}