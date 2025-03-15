package com.example.Sparta_Store.domain.point.repository;

import com.example.Sparta_Store.domain.point.entity.PointSummary;
import com.example.Sparta_Store.domain.users.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointSummaryRepository extends JpaRepository<PointSummary, Long> {
    List<PointSummary> findByUser(Users user);
}