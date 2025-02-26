package com.example.Sparta_Store.point.repository;

import com.example.Sparta_Store.point.entity.PointSummary;
import com.example.Sparta_Store.user.entity.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointSummaryRepository extends JpaRepository<PointSummary, Long> {
    List<PointSummary> findByUser(Users user);
}