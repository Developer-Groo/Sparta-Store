package com.example.Sparta_Store.point.service;

import com.example.Sparta_Store.point.TransactionType;
import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.point.entity.PointTransaction;
import com.example.Sparta_Store.point.repository.PointRepository;
import com.example.Sparta_Store.point.repository.PointTransactionRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    // 주문 완료 시 자동 포인트 적립
    @Transactional
    public void earnPoints(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        // 사용자 포인트 조회 (없으면 새로 생성)
        Point point = pointRepository.findByUser(user)
                .orElse(new Point(user, 0));

        point.addPoints(amount);
        pointRepository.save(point);

        // ✅ 포인트 적립 내역 저장
        PointTransaction transaction = new PointTransaction(user, amount, TransactionType.EARNED);
        pointTransactionRepository.save(transaction);

        log.info("포인트 {} 자동 적립 완료 (유저 ID: {})", amount, userId);
    }

}

