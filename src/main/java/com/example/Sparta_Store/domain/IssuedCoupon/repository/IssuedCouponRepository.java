package com.example.Sparta_Store.domain.IssuedCoupon.repository;

import com.example.Sparta_Store.domain.IssuedCoupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long>,
    IssuedCouponQueryRepository {

}
