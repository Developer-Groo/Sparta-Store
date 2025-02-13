package com.example.Sparta_Store.user.controller;

import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.dto.UserRequestDto;
import com.example.Sparta_Store.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto){
        CreateUserResponseDto userResponseDto =
                userService.signUp(
                        requestDto.email(),
                        requestDto.password(),
                        requestDto.name(),
                        requestDto.address()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponseDto);
    }
}
