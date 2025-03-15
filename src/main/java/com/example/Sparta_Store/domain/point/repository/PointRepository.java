package com.example.Sparta_Store.domain.point.repository;

import com.example.Sparta_Store.domain.point.entity.Point;
import com.example.Sparta_Store.domain.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(Users user);
}

