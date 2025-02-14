package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQueryRepository {
    Page<Item> findByName(String keyword, Pageable pageable);
}
