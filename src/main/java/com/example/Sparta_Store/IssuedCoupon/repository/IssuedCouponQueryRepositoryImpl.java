package com.example.Sparta_Store.IssuedCoupon.repository;

import static com.example.Sparta_Store.IssuedCoupon.entity.QIssuedCoupon.issuedCoupon;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
