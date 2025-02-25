package com.example.Sparta_Store.point.service;

import com.example.Sparta_Store.orders.event.OrderConfirmedEvent;
import com.example.Sparta_Store.point.TransactionType;
import com.example.Sparta_Store.point.dto.PointTransactionResponseDto;
import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.point.entity.PointTransaction;
import com.example.Sparta_Store.point.repository.PointRepository;
import com.example.Sparta_Store.point.repository.PointTransactionRepository;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;
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

        PointTransaction transaction = new PointTransaction(user, earnedPoints, TransactionType.EARNED); // 포인트 변동 내역 기록
        pointTransactionRepository.save(transaction); // 내용 저장

    }


    // 컨트롤러와 서비스에 중복되는 포인트 정보를 조회하는 메서드
    public Point getOrCreatePoint(Users user) {
        return pointRepository.findByUser(user) // 해당 유저의 포인트정보 조회
                .orElseGet(() -> {
                    return new Point(user, 0); // 포인트 정보가 없으면 새로 생성하고 잔액은 0
                });

    }

    // 포인트 적립 내역 조회 (DTO 변환 포함)
    @Transactional
    public List<PointTransactionResponseDto> getPointTransactions(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        List<PointTransaction> transactions = pointTransactionRepository.findByUser(user); // user조회

        return transactions.stream()
                .map(PointTransactionResponseDto::toEntity) // DTO 변환
                .collect(Collectors.toList());
    }
}

