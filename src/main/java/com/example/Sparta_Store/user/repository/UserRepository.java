package com.example.Sparta_Store.user.repository;

import com.example.Sparta_Store.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
