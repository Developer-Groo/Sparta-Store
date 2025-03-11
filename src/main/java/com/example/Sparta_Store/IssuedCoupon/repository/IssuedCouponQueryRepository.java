package com.example.Sparta_Store.IssuedCoupon.repository;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;

public interface IssuedCouponQueryRepository {

    boolean couponIssueCheck(Long userId, String name);

    IssuedCoupon findIssuedCoupon(Long userId, Long issuedCouponId);

    IssuedCoupon couponToUse(Long userId, Long issuedCouponId);
}
