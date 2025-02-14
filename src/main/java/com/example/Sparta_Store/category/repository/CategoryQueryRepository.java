package com.example.Sparta_Store.category.repository;

import com.example.Sparta_Store.category.entity.Category;

import java.util.List;

public interface CategoryQueryRepository {
    List<Category> getCategoryTree();
}
