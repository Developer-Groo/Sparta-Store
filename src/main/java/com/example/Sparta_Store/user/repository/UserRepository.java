package com.example.Sparta_Store.user.repository;

import com.example.Sparta_Store.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findByProviderAndProviderId(String provider, String providerId);
}
