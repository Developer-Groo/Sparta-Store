package com.example.Sparta_Store.domain.orderItem.repository;


import com.example.Sparta_Store.domain.orderItem.entity.OrderItem;
import com.example.Sparta_Store.util.QuerydslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.Sparta_Store.domain.item.entity.QItem.item;
import static com.example.Sparta_Store.domain.orderItem.entity.QOrderItem.orderItem;
import static com.example.Sparta_Store.domain.orders.entity.QOrders.orders;
import static com.example.Sparta_Store.domain.user.entity.QUsers.users;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderItemQueryRepositoryImpl implements OrderItemQueryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderItem> findByOrderId(String orderId, Pageable pageable) {
        JPAQuery<OrderItem> result = queryFactory
                .selectFrom(orderItem)
                .where(orderIdEquals(orderId));

        return QuerydslUtil.fetchPage(result, orderItem, pageable);
    }

    @Override
    public Optional<OrderItem> findOrderItemWithUserAndItem(Long userId, Long itemId) {
        OrderItem result = queryFactory
                .selectFrom(orderItem)
                .join(orderItem.orders, orders).fetchJoin()
                .join(orders.user, users).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .where(
                        orders.user.id.eq(userId),
                        orderItem.item.id.eq(itemId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    // orderId로 주문 상품 조회
    private BooleanExpression orderIdEquals(String orderId) {
        return orderId != null ? orderItem.orders.id.eq(orderId) : null;
    }

    @Override
    @Transactional
    public void deleteOrderItemsByOrderId(String orderId) {
        queryFactory.delete(orderItem)
            .where(orderItem.orders.id.eq(orderId))
            .execute();
    }

}
