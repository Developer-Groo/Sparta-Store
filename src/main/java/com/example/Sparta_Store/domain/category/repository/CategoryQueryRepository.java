package com.example.Sparta_Store.domain.category.repository;

import com.example.Sparta_Store.domain.category.entity.Category;

import java.util.List;

public interface CategoryQueryRepository {
    List<Category> getCategoryTree();
}
