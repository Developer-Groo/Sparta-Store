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
    public void earnPoints(Long userId, int amount) { // userid로 user를 찾고 amount < 구매금액의 10% 적립
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")); // userid로 사용자조회 및 예외발생

        // 사용자 포인트 조회 (없으면 새로 생성)
        Point point = getOrCreatePoint(user);

        point.addPoints(amount); // 기존 포인트에 추가
        pointRepository.save(point); // 변겅내용 저장

        // 포인트 적립 내역 저장
        PointTransaction transaction = new PointTransaction(user, amount, TransactionType.EARNED); //transaction은 거래내역 이라 내역을 생성하고
        pointTransactionRepository.save(transaction); // 내역을 저장
    }

    // 컨트롤러와 서비스에 중복되는 포인트 정보를 조회하는 메서드
    public Point getOrCreatePoint(User user) {
        return pointRepository.findByUser(user) // 해당 유저의 포인트정보 조회
                .orElseGet(() -> {
                    return new Point(user, 0); // 포인트 정보가 없으면 새로 생성하고 잔액은 0
                });
    }

}

