package com.example.Sparta_Store.likes.controller;

import com.example.Sparta_Store.likes.dto.request.LikesRequestDto;
import com.example.Sparta_Store.likes.dto.response.LikeResponseDto;
import com.example.Sparta_Store.likes.service.LikesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping
    public ResponseEntity<String> addLike(@RequestBody @Valid LikesRequestDto requestDto){
        likesService.addLike(requestDto.userId(), requestDto.itemId());
        return ResponseEntity.status(HttpStatus.OK).body("찜 추가가 되셨습니다.");
    }

    @GetMapping
    public ResponseEntity<List<LikeResponseDto>> getLikeList(@RequestParam Long userId) {
        List<LikeResponseDto> response = likesService.getLikeList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeLike(@PathVariable("itemId") Long itemId, @RequestParam Long userId) {
        likesService.removeLike(userId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body("찜 취소가 되었습니다.");
    }
}
