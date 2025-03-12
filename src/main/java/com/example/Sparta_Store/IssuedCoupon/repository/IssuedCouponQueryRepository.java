package com.example.Sparta_Store.IssuedCoupon.repository;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;

public interface IssuedCouponQueryRepository {

    IssuedCoupon couponToUse(Long userId, Long issuedCouponId);
}
