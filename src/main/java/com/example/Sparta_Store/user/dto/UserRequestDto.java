package com.example.Sparta_Store.user.dto;

import com.example.Sparta_Store.address.entity.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank @Email(message = "유효하지않은 이메일 형식입니다.")
        String email,
        @NotBlank
        @Size(min = 8, max = 12)
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]+$",
        message = "영문 대소문자, 숫자, 특수문자를 각각 최소 1글자씩 포함해야 합니다.")
        String password,
        @NotBlank
        String name,
        Address address
) {
}
