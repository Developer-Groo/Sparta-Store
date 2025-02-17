package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.util.QuerydslUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.example.Sparta_Store.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> findByNameAndStockCondition(boolean inStock, String keyword, Pageable pageable) {
        JPAQuery<Item> query = queryFactory
                .selectFrom(item)
                .where(
                        itemNameLike(keyword),
                        itemGtZero(inStock)
                );

        return QuerydslUtil.fetchPage(query, item, pageable);
    }

    @Override
    public Page<Item> findAllByStockCondition(boolean inStock, Pageable pageable) {
        JPAQuery<Item> query = queryFactory
                .selectFrom(item)
                .where(itemGtZero(inStock));

        return QuerydslUtil.fetchPage(query, item, pageable);
    }

    @Override
    public Page<Item> findByCategoryId(Long categoryId, boolean inStock, Pageable pageable) {
        JPAQuery<Item> query = queryFactory
                .selectFrom(item)
                .where(
                        item.category.id.eq(categoryId),
                        itemGtZero(inStock)
                );

        return QuerydslUtil.fetchPage(query, item, pageable);
    }

    private BooleanExpression itemNameLike(String keyword) {
        return keyword != null && !keyword.isEmpty() ? item.name.like("%" + keyword + "%") : null;
    }

    private BooleanExpression itemGtZero(boolean inStock) {
        return inStock ? item.stockQuantity.gt(0) : null;
    }
}
