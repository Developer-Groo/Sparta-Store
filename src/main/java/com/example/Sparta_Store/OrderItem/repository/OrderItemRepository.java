package com.example.Sparta_Store.OrderItem.repository;

import com.example.Sparta_Store.OrderItem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
