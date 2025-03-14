package com.example.Sparta_Store.coupon.service;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;
import com.example.Sparta_Store.IssuedCoupon.repository.IssuedCouponRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "CouponService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final IssuedCouponRepository issuedCouponRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * 쿠폰 발급
     * - 발급 기록 확인
     * - 쿠폰 발급 (성공 시, 발급 기록 작성)
     */
    private static final RedisScript<String> COUPON_SCRIPT = new DefaultRedisScript<>(
        "local count = redis.call('SCARD', KEYS[1]) " +
            "if count >= 1000 then return 'COUPON_EXHAUSTED' end " +
            "if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then return 'ALREADY_ISSUED' end " +
            "local coupon = redis.call('RPOP', KEYS[2]) " +
            "if not coupon then return 'COUPON_EXHAUSTED' end " +
            "redis.call('SADD', KEYS[1], ARGV[1]) " +
            "return coupon",
        String.class
    );

    public String getRandomCoupon(Long userId, String couponName) {
        String key = "issuedUser:" + couponName;

        // Lua 스크립트 실행
        String selectedCoupon = redisTemplate.execute(
            COUPON_SCRIPT,
            Arrays.asList(key, couponName),
            String.valueOf(userId)
        );

        // 결과 처리
        if (selectedCoupon.equals("ALREADY_ISSUED")) {
            return "이미 발급받은 쿠폰입니다.";
        } else if (selectedCoupon.equals("COUPON_EXHAUSTED")) {
            return "쿠폰이 모두 소진되었습니다.";
        }

        // 발급 이력 저장 - RabbitMQ 메시지 전송
        sendCouponIssuanceMessage(userId, couponName, selectedCoupon);
        return selectedCoupon + "원 할인 쿠폰이 발급되었습니다.";
    }

    // 쿠폰 발급 이력 DB 저장
    @Transactional
    public void saveCouponUser(Long userId, String couponName, Long selectedCoupon) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime expirationDate = now.with(LocalTime.MAX);
        IssuedCoupon issuedCoupon = IssuedCoupon.toEntity(couponName, selectedCoupon, userId, expirationDate);

        issuedCouponRepository.save(issuedCoupon);
    }

    // 쿠폰 발급 이력 DB 저장 메시지큐 전송
    public void sendCouponIssuanceMessage(Long userId, String couponName, String selectedCoupon) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("couponName", couponName);
        message.put("selectedCoupon", selectedCoupon);

        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        } catch (AmqpException e) {
            log.error("user: {}, 쿠폰 발급 메시지 전송 실패: {}", userId, message, e);
        }
    }

}
