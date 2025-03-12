package com.example.Sparta_Store.orders.repository;

import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersQueryRepository {

    Page<Orders> findOrders(
        Long userId,
        LocalDateTime startOfDate,
        LocalDateTime endOfDate,
        OrderStatus orderStatus,
        Pageable pageable
    );

    List<Orders> findOrdersForAutoConfirmation();

}
