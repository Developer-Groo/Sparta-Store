package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {

    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
}
