package com.example.Sparta_Store.coupon.service;

import com.example.Sparta_Store.coupon.exception.CouponErrorCode;
import com.example.Sparta_Store.couponUser.entity.CouponUser;
import com.example.Sparta_Store.couponUser.repository.CouponUserRepository;
import com.example.Sparta_Store.exception.CustomException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "CouponService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponUserRepository couponUserRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 쿠폰 발급
     * - 발급 기록 확인
     * - 쿠폰 발급 (성공 시, 발급 기록 작성)
     */
    @Transactional
    public String getRandomCoupon(Long userId, String couponName) {

        // 발급 이력 확인
        if (couponUserRepository.couponIssueCheck(userId, couponName)) {
            throw new CustomException(CouponErrorCode.EXISTS_COUPON_USER);
        }

        // 랜덤 쿠폰 발급
        Long size = redisTemplate.opsForList().size(couponName);
        if (size == null || size == 0) {
            log.error("userId: {} 쿠폰이 모두 소진되었습니다. size: {}", size);
            throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
        }
        int randomIndex = new Random().nextInt(size.intValue());
        List<String> coupons = redisTemplate.opsForList().range(couponName, randomIndex, randomIndex);
        if (coupons == null || coupons.isEmpty()) {
            log.error("userId: {} 쿠폰이 모두 소진되었습니다. randomIndex: {}", randomIndex);
            throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
        }
        String selectedCoupon = coupons.get(0);

        // 발급 이력 저장
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime expirationDate = now.with(LocalTime.MAX);
        CouponUser couponUser = new CouponUser(couponName, selectedCoupon, userId, expirationDate);

        couponUserRepository.save(couponUser);

        // 발급된 쿠폰 삭제
        redisTemplate.opsForList().remove(couponName, 1, selectedCoupon);

        log.info("user: {}, 받은 쿠폰: {}, 남은 쿠폰 수량: {}", userId, selectedCoupon, size-1);
        return selectedCoupon + " 쿠폰이 발급되었습니다.";
    }

}
