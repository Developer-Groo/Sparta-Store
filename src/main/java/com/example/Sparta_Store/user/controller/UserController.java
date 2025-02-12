package com.example.Sparta_Store.user.controller;

import com.example.Sparta_Store.user.dto.CreateUserResponseDto;
import com.example.Sparta_Store.user.dto.DeleteUserRequestDto;
import com.example.Sparta_Store.user.dto.UpdateInfoRequestDto;
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

    @PostMapping("/signUp")
    public ResponseEntity<CreateUserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto){
        CreateUserResponseDto userResponseDto =
                userService.signUp(
                        requestDto.email(),
                        requestDto.password(),
                        requestDto.name(),
                        requestDto.address()
                );

        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/password") // localhost:8080/users/delete?id=1 로 사용
    public ResponseEntity<String> updateUserPassword(@RequestParam Long id, @RequestBody UpdatePasswordRequestDto dto) { // securityConfig를 사용안하기 위해 param id 로 대체 json으로 oldPassword와 newPassword 를 받고 string을 반환하겠다

        userService.updatePassword(
                id,
                dto.oldPassword(),
                dto.newPassword()  // updatePassword 매소드에 3가지 를 받는다.
        );

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 수정이 완료되었습니다.");  // 적어놓은 메세지 반환
    }

    @PatchMapping("/info") // localhost:8080/users/info?id=1 사용 파람을 사용햇기에 ?id=1 이 추가댐
    public ResponseEntity<String> updateUserInfo(@RequestParam Long id , @RequestBody UpdateInfoRequestDto dto) { // 위와 같음

        userService.updateInfo(id, dto.name(), dto.address());

        return ResponseEntity.status(HttpStatus.OK).body("회원 정보 수정이 완료되었습니다.");

    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUserId(@RequestParam Long id, @RequestBody DeleteUserRequestDto dto) {

        userService.deleteUser(id, dto.password());

        return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴가 왼료되었습니다.");
    }
}
