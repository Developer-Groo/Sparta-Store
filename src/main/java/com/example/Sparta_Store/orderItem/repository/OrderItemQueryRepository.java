package com.example.Sparta_Store.orderItem.repository;

import com.example.Sparta_Store.orderItem.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderItemQueryRepository {

    Page<OrderItem> findByOrderId(String orderId, Pageable pageable);

    Optional<OrderItem> findOrderItemWithUserAndItem(Long userId, Long itemId);

    Long findOrderItemQuantity(String orderId);
}
