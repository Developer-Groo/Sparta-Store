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
    public Page<Item> findByName(String keyword, Pageable pageable) {
        JPAQuery<Item> result = queryFactory
                .selectFrom(item)
                .where(itemNameLike(keyword));

        return QuerydslUtil.fetchPage(result, item, pageable);
    }

    private BooleanExpression itemNameLike(String keyword) {
        return keyword != null && !keyword.isEmpty() ? item.name.like("%" + keyword + "%") : null;
    }

}
