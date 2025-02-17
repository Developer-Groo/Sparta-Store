package com.example.Sparta_Store.orderItem.repository;

import com.example.Sparta_Store.orderItem.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemQueryRepository {

    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

}
