package com.example.Sparta_Store.cart.repository;

import com.example.Sparta_Store.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
