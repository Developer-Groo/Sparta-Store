package com.example.Sparta_Store.category.repository;

import com.example.Sparta_Store.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryQueryRepository {
}
