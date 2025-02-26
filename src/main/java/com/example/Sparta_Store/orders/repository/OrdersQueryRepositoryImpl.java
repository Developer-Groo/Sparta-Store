package com.example.Sparta_Store.orders.repository;

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
    public Page<Orders> findByUserId(Long userId, Pageable pageable) {
        JPAQuery<Orders> result = queryFactory.selectFrom(orders).where(userIdEquals(userId));

        return QuerydslUtil.fetchPage(result, orders, pageable);
    }

    // userId로 주문 검색
    private BooleanExpression userIdEquals(Long userId) {
        return userId != null ? orders.user.id.eq(userId) : null;
    }

    //TODO 서버가 배포된 환경에서는 시간대가 다를 수 있음 -> UTC 또는 원하는 시간대로 고정

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

    /**
     * 생성된 지 10분 이상이 지난 주문 중에서 '결제전' 상태인 주문 조회
     */
    @Override
    public List<Orders> findOrdersForAutoCancellation() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        return queryFactory.selectFrom(orders)
            .where(orders.orderStatus.eq(OrderStatus.BEFORE_PAYMENT)
                .and(orders.createdAt.loe(tenMinutesAgo)))
            .fetch();
    }

}
