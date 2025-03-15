package com.example.Sparta_Store.domain.login.repository;

import com.example.Sparta_Store.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
