package com.example.Sparta_Store.orders.repository;

import static com.example.Sparta_Store.orders.OrderStatus.BEFORE_PAYMENT;
import static com.example.Sparta_Store.orders.OrderStatus.PAYMENT_CANCELLED;
import static com.example.Sparta_Store.orders.entity.QOrders.orders;

import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.util.QuerydslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrdersQueryRepositoryImpl implements OrdersQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Orders> findOrders(
        Long userId,
        LocalDateTime startOfDate,
        LocalDateTime endOfDate,
        OrderStatus orderStatus,
        Pageable pageable
    ) {
        JPAQuery<Orders> result = queryFactory.selectFrom(orders).where(
            userIdEquals(userId).and(orders.orderStatus.notIn(BEFORE_PAYMENT, PAYMENT_CANCELLED)),
            (statusEquals(orderStatus)),
            createdAtBetween(startOfDate, endOfDate)
        );

        return QuerydslUtil.fetchPage(result, orders, pageable);
    }

    // userId로 주문 검색
    private BooleanExpression userIdEquals(Long userId) {
        return userId != null ? orders.user.id.eq(userId) : null;
    }
    // 주문상태 조건 검색
    private BooleanExpression statusEquals(OrderStatus orderStatus) {
        return orderStatus != null ? orders.orderStatus.eq(orderStatus) : null;
    }
    // 주문날짜 조건 검색
    private BooleanExpression createdAtBetween(LocalDateTime startOfDate, LocalDateTime endOfDate) {
        if (startOfDate == null && endOfDate == null) {
            return null;
        } if (startOfDate != null && endOfDate != null) {
            return orders.createdAt.between(startOfDate, endOfDate);
        } else if (startOfDate != null) {
            return orders.createdAt.goe(startOfDate);
        } else {
            return orders.createdAt.loe(endOfDate);
        }
    }

    /**
     * 주문 상태가 "배송완료" 이며, 업데이트 날짜가 조회시점보다 5일 이상 지난 주문 조회
     */
    @Override
    public List<Orders> findOrdersForAutoConfirmation() {
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

        return queryFactory.selectFrom(orders)
            .where(orders.orderStatus.eq(OrderStatus.DELIVERED)
                .and(orders.updatedAt.loe(fiveDaysAgo)))
            .fetch();
    }

}
