package com.example.Sparta_Store.point.controller;

import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.point.repository.PointRepository;
import com.example.Sparta_Store.point.repository.PointTransactionRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;


    //사용자의 현재 포인트 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getUserPoints(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        Point point = pointRepository.findByUser(user)
                .orElse(new Point(user, 0));

        return ResponseEntity.ok(point.getBalance());
    }
}
