package com.example.Sparta_Store.domain.category.repository;

import com.example.Sparta_Store.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryQueryRepository {
}
