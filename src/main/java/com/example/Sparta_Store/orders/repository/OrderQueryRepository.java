package com.example.Sparta_Store.orders.repository;

import static com.example.Sparta_Store.orders.entity.QOrders.orders;

import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.util.QuerydslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Orders> findByUserId(Long userId, Pageable pageable) {
        JPAQuery<Orders> result = queryFactory.selectFrom(orders).where(userIdEqual(userId));

        return QuerydslUtil.fetchPage(result, orders, pageable);
    }

    // userId로 주문 검색
    private BooleanExpression userIdEqual(Long userId) {
        return userId != null ? orders.id.eq(userId) : null;
    }

}
