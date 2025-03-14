package com.example.Sparta_Store.domain.cart.repository;

import com.example.Sparta_Store.domain.cart.entity.Cart;
import com.example.Sparta_Store.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(Users user);

    // user_id 컬럼 값이 userId인 Cart 조회
    Optional<Cart> findByUserId(Long userId);

    Long user(Users user);

}
