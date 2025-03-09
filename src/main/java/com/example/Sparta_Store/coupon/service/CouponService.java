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
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;

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
        CouponUser couponUser = CouponUser.toEntity(couponName, selectedCoupon, userId, expirationDate);

        couponUserRepository.save(couponUser);

        // 발급된 쿠폰 삭제
        redisTemplate.opsForList().remove(couponName, 1, selectedCoupon);

        log.info("user: {}, 받은 쿠폰: {}, 남은 쿠폰 수량: {}", userId, selectedCoupon, size-1);
        return selectedCoupon + " 쿠폰이 발급되었습니다.";
    }

    @Transactional
    public String getRandomCouponV2(Long userId, String couponName) {

        // 발급 이력 확인
        if (couponUserRepository.couponIssueCheck(userId, couponName)) {
            throw new CustomException(CouponErrorCode.EXISTS_COUPON_USER);
        }
        // 쿠폰 발급
        String selectedCoupon = redisTemplate.opsForList().rightPop(couponName);
        if (selectedCoupon == null) {
            log.error("user: {} 쿠폰이 모두 소진되었습니다.", userId);
            throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
        }
        // 발급 이력 저장
        saveCouponUser(userId, couponName, selectedCoupon);
        log.info("user: {}, 받은 쿠폰: {}", userId, selectedCoupon);
        return selectedCoupon;

    }

//    @Transactional
//    public String getRandomCouponV2(Long userId, String couponName) {
//
//        // 발급 이력 확인
//        if (couponUserRepository.couponIssueCheck(userId, couponName)) {
//            throw new CustomException(CouponErrorCode.EXISTS_COUPON_USER);
//        }
//
//        Long size = redisTemplate.opsForList().size(couponName);
//        if (size == null || size == 0) {
//            log.error("user: {} 쿠폰이 모두 소진되었습니다.", userId);
//            throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
//        }
//
//        RLock lock = redissonClient.getLock("couponLock:" + couponName);
//        boolean isLocked = false;
//        try {
//            isLocked = lock.tryLock(3, 1, TimeUnit.SECONDS);
//            if (!isLocked) {
//                log.error("user: {}, 쿠폰 발급을 위한 락 획득 실패", userId);
//                throw new CustomException(CouponErrorCode.COUPON_BUSY);
//            }
//
//            String selectedCoupon = redisTemplate.opsForList().rightPop(couponName);
//            if (selectedCoupon == null) {
//                log.error("user: {} 쿠폰이 모두 소진되었습니다.", userId);
//                throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
//            }
//
//            // 발급 이력 DB 저장 TODO 비동기
////            saveCouponUser(userId, couponName, selectedCoupon);
//
////            Long remainingSize = redisTemplate.opsForList().size(couponName);
////            log.info("user: {}, 받은 쿠폰: {}, 남은 쿠폰 수량: {}", userId, selectedCoupon, remainingSize);
//            log.info("user: {}, 받은 쿠폰: {}", userId, selectedCoupon);
//            return selectedCoupon + " 쿠폰이 발급되었습니다.";
//        } catch (Exception e) {
//            Thread.currentThread().interrupt();
//            throw new CustomException(CouponErrorCode.TRY_AGAIN_LATER);
//        } finally {
////            if (isLocked && Thread.currentThread().isInterrupted()) {
////                try {
////                    lock.unlock();
////                } catch (Exception e) {
////                    log.error("Failed to unlock the lock", e);
////                }
////            }
//            lock.unlock();
//        }
//
//    }



//    @Transactional
//    public String getRandomCouponV3(Long userId, String couponName) {
//
//        // 발급 이력 확인
//        if (couponUserRepository.couponIssueCheck(userId, couponName)) {
//            throw new CustomException(CouponErrorCode.EXISTS_COUPON_USER);
//        }
//
//        // Lua 스크립트 실행
//        String script =
//            "local size = redis.call('LLEN', KEYS[1]) " +
//                "if size == 0 then " +
//                "return {err='쿠폰이 모두 소진되었습니다.'} " +
//                "end " +
//                "local randomIndex = math.random(0, size - 1) " +
//                "local coupon = redis.call('LINDEX', KEYS[1], randomIndex) " +
//                "if coupon == nil then " +
//                "return {err='쿠폰이 모두 소진되었습니다.'} " +
//                "end " +
//                "redis.call('LREM', KEYS[1], 1, coupon) " +
//                "return coupon";
//
//        // 스크립트 실행
//        RScript rScript = redissonClient.getScript();
//        List<Object> result = rScript.eval(RScript.Mode.READ_WRITE,
//            script, RScript.ReturnType.MULTI, Arrays.asList(couponName));
//
//        if (result == null || result.isEmpty()) {
//            throw new CustomException(CouponErrorCode.COUPON_EXHAUSTED);
//        }
//
//        String selectedCoupon = (String) result.get(0);
//
//        // 발급 이력 저장
//        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
//        LocalDateTime expirationDate = now.with(LocalTime.MAX);
//        CouponUser couponUser = CouponUser.toEntity(couponName, selectedCoupon, userId, expirationDate);
//
//        couponUserRepository.save(couponUser);
//
//        log.info("user: {}, 받은 쿠폰: {}", userId, selectedCoupon);
//        return selectedCoupon + " 쿠폰이 발급되었습니다.";
//    }

    // 쿠폰 발급 이력 저장
    @Transactional
    public void saveCouponUser(Long userId, String couponName, String selectedCoupon) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime expirationDate = now.with(LocalTime.MAX);
        CouponUser couponUser = CouponUser.toEntity(couponName, selectedCoupon, userId, expirationDate);

        couponUserRepository.save(couponUser);
    }
}
