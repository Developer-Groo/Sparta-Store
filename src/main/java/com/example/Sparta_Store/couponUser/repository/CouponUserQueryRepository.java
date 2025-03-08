package com.example.Sparta_Store.couponUser.repository;

import com.example.Sparta_Store.couponUser.entity.CouponUser;

public interface CouponUserQueryRepository {

    boolean couponIssueCheck(Long userId, String name);

    CouponUser findCouponUser(Long userId, String name);
}
