package com.example.Sparta_Store.orders.repository;

import com.example.Sparta_Store.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long>, OrdersQueryRepository {

}
