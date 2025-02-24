package com.example.Sparta_Store.user.repository;

import com.example.Sparta_Store.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Users findByProviderAndProviderId(String provider, String providerId);
}
