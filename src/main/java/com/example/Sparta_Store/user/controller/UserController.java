package com.example.Sparta_Store.user.controller;

import com.example.Sparta_Store.user.dto.UpdatePasswordRequestDto;
import com.example.Sparta_Store.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestParam Long id, @RequestBody UpdatePasswordRequestDto dto) {
        userService.updatePassword(
                id,
                dto.oldPassword(),
                dto.newPassword()
        );

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 변경이 완료되었습니다.");
    }
}
