package com.example.Sparta_Store.domain.likes.controller;

import com.example.Sparta_Store.domain.likes.dto.request.LikesRequestDto;
import com.example.Sparta_Store.domain.likes.dto.response.LikeResponseDto;
import com.example.Sparta_Store.domain.likes.service.LikesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addLike(@RequestBody @Valid LikesRequestDto requestDto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        likesService.addLike(requestDto.itemId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "찜 추가가 되셨습니다."));
    }

    @GetMapping
    public ResponseEntity<List<LikeResponseDto>> getLikeList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        List<LikeResponseDto> response = likesService.getLikeList(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Long> getLikeCount(@PathVariable("itemId") Long itemId) {
        Long count = likesService.getLikeCount(itemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(count);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, String>> removeLike(@PathVariable("itemId") Long itemId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        likesService.removeLike(itemId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "찜 취소가 되었습니다."));
    }
}
