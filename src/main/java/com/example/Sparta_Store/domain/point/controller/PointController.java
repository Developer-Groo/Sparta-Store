package com.example.Sparta_Store.domain.point.controller;

import com.example.Sparta_Store.domain.point.dto.PointSummaryResponseDto;
import com.example.Sparta_Store.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getUserPoints(@PathVariable("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.getOrCreatePoint(userId));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<PointSummaryResponseDto>> getPointSummary(@PathVariable("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pointService.getPointSummaries(userId));
    }
}
