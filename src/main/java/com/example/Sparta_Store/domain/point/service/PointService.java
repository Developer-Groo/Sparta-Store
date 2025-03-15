package com.example.Sparta_Store.domain.point.service;

import com.example.Sparta_Store.domain.orders.event.OrderConfirmedEvent;
import com.example.Sparta_Store.domain.point.SummaryType;
import com.example.Sparta_Store.domain.point.dto.PointSummaryResponseDto;
import com.example.Sparta_Store.domain.point.entity.Point;
import com.example.Sparta_Store.domain.point.entity.PointSummary;
import com.example.Sparta_Store.domain.point.repository.PointRepository;
import com.example.Sparta_Store.domain.point.repository.PointSummaryRepository;
import com.example.Sparta_Store.domain.users.entity.Users;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointSummaryRepository pointSummaryRepository;
    private final UserRepository userRepository;

    // 주문 확정 이벤트를 감지하여 포인트 적립
    @EventListener
    @Transactional // 주문상태가 구매확정이 되면 실행
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {
        Users user = userRepository.findById(event.getUserId()) // user를 조회
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")); // 예외

        long earnedPoints = (long) (event.getTotalPrice() * 0.1); // 총 주문 금액의 10% 적립
        log.info("포인트 적립 이벤트 감지 - 유저 ID: {}, 적립 포인트: {}", event.getUserId(), earnedPoints);

        Point point = pointRepository.findByUser(user) // user의 포인트 조회
                .orElseGet(() -> {
                    Point newPoint = new Point(user, 0);
                    return pointRepository.save(newPoint);
                }); // 신규 주문이면 새로 생성

        point.addPoints(earnedPoints); // 기존 포인트에 추가
        pointRepository.save(point); // 변경 내용 저장

        PointSummary summary = new PointSummary(user, earnedPoints, SummaryType.EARNED); // 포인트 변동 내역 기록
        pointSummaryRepository.save(summary); // 내용 저장
    }

    @Transactional
    public int getOrCreatePoint(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        Point point = pointRepository.findByUser(user)
                .orElseGet(() -> new Point(user, 0));

        return point.getBalance();
    }

    @Transactional
    public List<PointSummaryResponseDto> getPointSummaries(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        List<PointSummary> summaries = pointSummaryRepository.findByUser(user);

        return summaries.stream()
                .map(PointSummaryResponseDto::toEntity)
                .toList();
    }
}

