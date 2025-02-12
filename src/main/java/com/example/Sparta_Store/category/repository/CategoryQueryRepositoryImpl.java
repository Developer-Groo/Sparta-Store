package com.example.Sparta_Store.category.repository;

import com.example.Sparta_Store.category.entity.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.Sparta_Store.category.entity.QCategory.category;

@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> getCategoryTree() {
        return queryFactory
                .selectFrom(category)
                .leftJoin(category.children).fetchJoin()
                .where(category.parent.isNull())
                .orderBy(category.id.asc()) // 카테고리 id 순으로 정렬
                .fetch();
    }
}
