package com.example.Sparta_Store.OrderItem.repository;

import static com.example.Sparta_Store.OrderItem.entity.QOrderItem.orderItem;

import com.example.Sparta_Store.OrderItem.entity.OrderItem;
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
public class OrderItemQueryRepositoryImpl implements OrderItemQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderItem> findByOrderId(Long orderId, Pageable pageable) {
        JPAQuery<OrderItem> result = queryFactory.selectFrom(orderItem).where(orderIdEquals(orderId));

        return QuerydslUtil.fetchPage(result, orderItem, pageable);
    }

    // orderId로 주문 상품 조회
    private BooleanExpression orderIdEquals(Long orderId) {
        return orderId != null ? orderItem.orders.id.eq(orderId) : null;
    }

}
