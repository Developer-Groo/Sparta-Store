package com.example.Sparta_Store.IssuedCoupon.repository;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long>,
    IssuedCouponQueryRepository {

}
