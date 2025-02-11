package com.example.Sparta_Store.user.controller;

import com.example.Sparta_Store.common.entity.MessageResponse;
import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.dto.UpdateInformationRequestDto;
import com.example.Sparta_Store.user.dto.UpdatePasswordRequestDto;
import com.example.Sparta_Store.user.dto.UserRequestDto;
import com.example.Sparta_Store.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
//
//    @PostMapping("/delete")
//    public ResponseEntity<MessageResponse> deleteUser(@RequestParam Long id, String password) {
//        userService.deleteUser(id, password);
//
//        return ResponseEntity.ok(new MessageResponse("유저 삭제가 완료되었습니다."));
//    }

    @PostMapping("/signUp")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody UserRequestDto requestDto){
        CreateUserResponseDto userResponseDto =
                userService.signUp(
                        requestDto.email(),
                        requestDto.password(),
                        requestDto.name(),
                        requestDto.address()
                );

        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> updatePassword(@RequestParam Long id, @RequestBody UpdatePasswordRequestDto dto) {
        userService.updateUserPassword(id, dto.getOldPassword(), dto.getNewPassword());

        return ResponseEntity.ok(new MessageResponse("비밀번호 수정이 완료되었습니다."));
    }

    @PatchMapping("/information")
    public ResponseEntity<MessageResponse> updateInformation(@RequestParam Long id, @RequestBody UpdateInformationRequestDto dto) {
        userService.updateUserInformation(id, dto.getUserName(), dto.getAddress());

        return ResponseEntity.ok(new MessageResponse("유저 정보 수정이 완료되었습니다."));
    }
}
