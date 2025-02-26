package com.example.Sparta_Store.point.repository;

import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.user.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(Users user);
}

