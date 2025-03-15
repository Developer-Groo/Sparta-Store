package com.example.Sparta_Store.domain.IssuedCoupon.repository;


import com.example.Sparta_Store.domain.IssuedCoupon.entity.IssuedCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.example.Sparta_Store.domain.IssuedCoupon.entity.QIssuedCoupon.issuedCoupon;

@Repository
@RequiredArgsConstructor
public class IssuedCouponQueryRepositoryImpl implements IssuedCouponQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 주문에 적용할 쿠폰
    @Override
    public IssuedCoupon couponToUse(Long userId, Long issuedCouponId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return queryFactory.selectFrom(issuedCoupon)
            .where(issuedCoupon.userId.eq(userId)
                .and(issuedCoupon.id.eq(issuedCouponId))
                .and(issuedCoupon.isUsed.eq(false))
                .and(issuedCoupon.expirationDate.isNull()
                    .or(issuedCoupon.expirationDate.goe(now))))
            .fetchOne();
    }
}
