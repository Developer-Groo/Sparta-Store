package com.example.Sparta_Store.cartItem.repository;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
