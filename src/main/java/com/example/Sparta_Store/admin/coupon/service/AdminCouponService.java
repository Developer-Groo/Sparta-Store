package com.example.Sparta_Store.admin.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AdminCouponService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCouponService {

    private final RedisTemplate redisTemplate;

    public void createCoupons(String couponName) {

        for (int i = 0; i < 6; i++) {
            redisTemplate.opsForList().rightPush(couponName, 1000);
        }

        for (int i = 0; i < 3; i++) {
            redisTemplate.opsForList().rightPush(couponName, 5000);
        }

        for (int i = 0; i < 1; i++) {
            redisTemplate.opsForList().rightPush(couponName, 10000);
        }
        log.info("{} 쿠폰 생성 완료", couponName);
    }
}
