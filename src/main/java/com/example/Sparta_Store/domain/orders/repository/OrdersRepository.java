package com.example.Sparta_Store.domain.orders.repository;

import com.example.Sparta_Store.domain.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, String>, OrdersQueryRepository {

}
