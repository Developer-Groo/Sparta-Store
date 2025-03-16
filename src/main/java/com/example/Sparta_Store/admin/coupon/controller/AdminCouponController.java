package com.example.Sparta_Store.admin.coupon.controller;

import com.example.Sparta_Store.admin.coupon.service.AdminCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    @PostMapping
    public ResponseEntity<String> createCoupons(@RequestParam String couponName) {
        adminCouponService.createCoupons(couponName);

        return ResponseEntity.status(HttpStatus.OK).body(couponName + " 쿠폰이 생성되었습니다.");
    }
}
