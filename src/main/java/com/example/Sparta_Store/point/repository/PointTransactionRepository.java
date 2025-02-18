package com.example.Sparta_Store.point.repository;

import com.example.Sparta_Store.point.entity.PointTransaction;
import com.example.Sparta_Store.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUser(User user);
}

