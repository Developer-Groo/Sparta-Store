package com.example.Sparta_Store.orderItem.repository;

import static com.example.Sparta_Store.item.entity.QItem.item;
import static com.example.Sparta_Store.orderItem.entity.QOrderItem.orderItem;
import static com.example.Sparta_Store.orders.entity.QOrders.orders;
import static com.example.Sparta_Store.user.entity.QUsers.users;

import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.util.QuerydslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderItemQueryRepositoryImpl implements OrderItemQueryRepository{

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

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

    // orderItem 갯수 조회
    @Override
    public Long findOrderItemQuantity(String orderId) {
        return queryFactory.select(orderItem.count()).from(orderItem)
            .where(orderItem.orders.id.eq(orderId))
            .fetchOne();
    }

    @Override
    @Transactional
    public void deleteOrderItemsByOrderId(String orderId) {
        queryFactory.delete(orderItem)
            .where(orderItem.orders.id.eq(orderId))
            .execute();
    }

}
