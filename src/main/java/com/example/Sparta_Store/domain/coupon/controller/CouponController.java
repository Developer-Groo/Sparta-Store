package com.example.Sparta_Store.domain.coupon.controller;

import com.example.Sparta_Store.domain.coupon.service.CouponService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.ZoneId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    /**
     * 랜덤 쿠폰 발급
     */
    @PostMapping
    public ResponseEntity<String> getRandomCoupon(HttpServletRequest request, @RequestParam String couponName) {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
        if (!isTimeInRange(now, now.with(LocalTime.NOON), now.with(LocalTime.MAX))) { // 12:00:00 ~ 23:59:59
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 발급 가능 시간이 아닙니다.");
        }

        Long userId = (Long) request.getAttribute("id");

        return ResponseEntity.status(HttpStatus.OK).body(couponService.getRandomCoupon(userId, couponName));
    }


    // ------- TEST 용
    @PostMapping("/test")
    public ResponseEntity<String> getRandomCouponV2(@RequestParam Long userId, @RequestParam String couponName) {

        return ResponseEntity.status(HttpStatus.OK).body(couponService.getRandomCoupon(userId, couponName));
    }

    public static boolean isTimeInRange(LocalTime now, LocalTime start, LocalTime end) {
        return now.isAfter(start) && now.isBefore(end);
    }

}
