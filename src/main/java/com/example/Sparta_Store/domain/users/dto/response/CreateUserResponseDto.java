package com.example.Sparta_Store.domain.users.dto.response;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.users.entity.Users;

public record CreateUserResponseDto(
    String email,
    String name,
    Address address
) {
    public static CreateUserResponseDto toDto(Users user) {
        return new CreateUserResponseDto(
            user.getEmail(),
            user.getName(),
            user.getAddress()
        );
    }
}