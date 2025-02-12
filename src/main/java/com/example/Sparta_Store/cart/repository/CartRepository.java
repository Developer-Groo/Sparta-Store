package com.example.Sparta_Store.cart.repository;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    // user_id 컬럼 값이 userId인 Cart 조회
    Optional<Cart> findByUserId(Long userId);

    Long user(User user);

}
