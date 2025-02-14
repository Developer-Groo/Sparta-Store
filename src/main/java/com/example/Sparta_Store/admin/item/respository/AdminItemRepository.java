package com.example.Sparta_Store.admin.item.respository;

import com.example.Sparta_Store.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminItemRepository extends JpaRepository<Item,Long> {
}
