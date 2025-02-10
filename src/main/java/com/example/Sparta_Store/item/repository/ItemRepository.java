package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
