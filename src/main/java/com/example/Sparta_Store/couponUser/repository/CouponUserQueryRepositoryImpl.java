package com.example.Sparta_Store.couponUser.repository;

import static com.example.Sparta_Store.couponUser.entity.QCouponUser.couponUser;

import com.example.Sparta_Store.couponUser.entity.CouponUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponUserQueryRepositoryImpl implements CouponUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 쿠폰 이름, userId 로 쿠폰 발급 여부 확인
    @Override
    public boolean couponIssueCheck(Long userId, String couponName) {

        // true: 발급 이력 o , false: 발급 이력 x
        return queryFactory.selectOne()
            .from(couponUser)
            .where(couponUser.userId.eq(userId)
                .and(couponUser.name.eq(couponName)))
            .fetchOne() != null;
    }

    // 쿠폰 이름, userId 로 쿠폰 발급 이력 검색
    @Override
    public CouponUser findCouponUser(Long userId, String name) {

        return queryFactory.selectFrom(couponUser)
            .where(couponUser.userId.eq(userId)
                .and(couponUser.name.eq(name)))
            .fetchOne();
    }
}
