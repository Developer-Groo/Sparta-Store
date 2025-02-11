package com.example.Sparta_Store.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdatePasswordRequestDto {

    private final String oldPassword;

    private final String newPassword;
}
