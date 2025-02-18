package com.example.Sparta_Store.point.repository;

import com.example.Sparta_Store.point.entity.Point;
import com.example.Sparta_Store.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);
}

