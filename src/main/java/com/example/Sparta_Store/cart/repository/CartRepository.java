package com.example.Sparta_Store.cart.repository;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
