package com.example.Sparta_Store.user.repository;

import com.example.Sparta_Store.user.entity.User;
import org.hibernate.annotations.NotFound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    default User findByIdOrElseThrow(long userId) {
        return findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }
}
