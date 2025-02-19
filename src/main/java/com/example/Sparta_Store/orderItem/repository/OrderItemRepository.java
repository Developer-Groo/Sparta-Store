package com.example.Sparta_Store.orderItem.repository;

import com.example.Sparta_Store.orderItem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemQueryRepository {

}
