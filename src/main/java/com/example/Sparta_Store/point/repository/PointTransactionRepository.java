package com.example.Sparta_Store.point.repository;

import com.example.Sparta_Store.point.entity.PointTransaction;
import com.example.Sparta_Store.user.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUser(Users user);
}