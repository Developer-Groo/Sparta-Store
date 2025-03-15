package com.example.Sparta_Store.domain.item.repository;

import com.example.Sparta_Store.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQueryRepository {

    Page<Item> findAllByStockCondition(boolean inStock, Pageable pageable);

    Page<Item> findByNameAndStockCondition(boolean inStock, String keyword, Pageable pageable);

    Page<Item> findByCategoryId(Long categoryId, boolean inStock, Pageable pageable);
}
