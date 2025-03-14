package com.example.Sparta_Store.admin.item.respository;

import com.example.Sparta_Store.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminItemRepository extends JpaRepository<Item,Long> {
}
