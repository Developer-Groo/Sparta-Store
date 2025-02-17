package com.example.Sparta_Store.OrderItem.repository;

import com.example.Sparta_Store.OrderItem.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemQueryRepository {

    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

}
