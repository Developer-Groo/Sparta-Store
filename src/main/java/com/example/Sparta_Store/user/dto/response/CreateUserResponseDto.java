package com.example.Sparta_Store.user.dto.response;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.user.entity.User;

public record CreateUserResponseDto(
    String email,
    String name,
    Address address
) {
    public static CreateUserResponseDto toDto(User user) {
        return new CreateUserResponseDto(
            user.getEmail(),
            user.getName(),
            user.getAddress()
        );
    }
}