package com.example.Sparta_Store.domain.orderItem.repository;

import com.example.Sparta_Store.domain.orderItem.entity.OrderItem;
import com.example.Sparta_Store.domain.orders.entity.Orders;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemQueryRepository {

    @EntityGraph(attributePaths = {"item"})
    Optional<List<OrderItem>> findOrderItemsByOrders(Orders order);

}
