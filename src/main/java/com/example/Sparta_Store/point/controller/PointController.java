package com.example.Sparta_Store.point.controller;

import com.example.Sparta_Store.point.dto.PointSummaryResponseDto;
import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.point.service.PointService;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final UserRepository userRepository;
    private final PointService pointService;


    //사용자의 현재 포인트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getUserPoints(@PathVariable Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")); //userid로 사용자 조회 및 예외처리

        Point point = pointService.getOrCreatePoint(user); // 포인트 정보를 가져오거나 없으면 만들기

        return ResponseEntity.status(HttpStatus.OK).body(point.getBalance()); // 코드컨벤션에 맞춰 응답
    }

    // 사용자의 포인트 내역 조회
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<PointSummaryResponseDto>> getPointSummary(@PathVariable Long userId) {

        List<PointSummaryResponseDto> summary = pointService.getPointSummaries(userId); // 포인트 변동 내역을 가져오기

        return ResponseEntity.status(HttpStatus.OK).body(summary);
    }
}
