package com.example.Sparta_Store.domain.IssuedCoupon.repository;

import com.example.Sparta_Store.domain.IssuedCoupon.entity.IssuedCoupon;

public interface IssuedCouponQueryRepository {

    IssuedCoupon couponToUse(Long userId, Long issuedCouponId);
}
