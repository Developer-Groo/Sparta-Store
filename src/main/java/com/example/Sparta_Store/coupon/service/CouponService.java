package com.example.Sparta_Store.coupon.service;

import com.example.Sparta_Store.couponUser.entity.CouponUser;
import com.example.Sparta_Store.couponUser.repository.CouponUserRepository;
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

    private final CouponUserRepository couponUserRepository;
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
            "if count < 400 then " +
            "    if redis.call('SADD', KEYS[1], ARGV[1]) == 1 then " +
            "        local coupon = redis.call('RPOP', KEYS[2]) " +
            "        if coupon then " +
            "            return coupon " +
            "        else " +
            "            return nil " +  // 쿠폰 소진 시 nil 반환
            "        end " +
            "    else " +
            "        return nil " +  // 이미 발급된 사용자
            "    end " +
            "else " +
            "    return nil " +  // 400개 이상일 경우 쿠폰 소진
            "end",
        String.class
    );

//    @Transactional
    public String getRandomCoupon(Long userId, String couponName) {
        String key = couponName + ":issuedUser";

        // Lua 스크립트 실행
        String selectedCoupon = redisTemplate.execute(
            COUPON_SCRIPT,
            Arrays.asList(key, couponName),
            String.valueOf(userId)
        );

        // 결과 처리
        if (selectedCoupon == null) {
            // 발급 이력이 있거나 쿠폰이 소진된 경우
            Boolean isMember = redisTemplate.opsForSet().isMember(key, String.valueOf(userId));
            if (Boolean.TRUE.equals(isMember)) {
//                log.error("user: {} 이미 발급받은 쿠폰입니다.", userId);
                return "이미 발급받은 쿠폰입니다.";
            } else {
//                log.error("user: {} 쿠폰이 모두 소진되었습니다.", userId);
                return "쿠폰이 모두 소진되었습니다.";
            }
        }
        // 발급 이력 저장 - RabbitMQ 메시지 전송
        sendCouponIssuanceMessage(userId, couponName, selectedCoupon);
//        saveCouponUser(userId, couponName, selectedCoupon);

        log.info("user: {}, 받은 쿠폰: {}", userId, selectedCoupon);
        return selectedCoupon + "쿠폰이 발급되었습니다.";
    }

    // 쿠폰 발급 이력 DB 저장
    @Transactional
    public void saveCouponUser(Long userId, String couponName, String selectedCoupon) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime expirationDate = now.with(LocalTime.MAX);
        CouponUser couponUser = CouponUser.toEntity(couponName, selectedCoupon, userId, expirationDate);

        couponUserRepository.save(couponUser);
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
