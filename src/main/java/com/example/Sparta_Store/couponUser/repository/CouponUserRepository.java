package com.example.Sparta_Store.couponUser.repository;

import com.example.Sparta_Store.couponUser.entity.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long>, CouponUserQueryRepository {

}
