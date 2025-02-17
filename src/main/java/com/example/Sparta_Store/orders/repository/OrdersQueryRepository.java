package com.example.Sparta_Store.orders.repository;

import com.example.Sparta_Store.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersQueryRepository {

    Page<Orders> findByUserId(Long userId, Pageable pageable);

}
