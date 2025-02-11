package com.example.Sparta_Store.cartItem.repository;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // cart_id 컬럼 값이 cartId인 모든 CartItem 조회
    Optional<List<CartItem>> findByCartId(Long cartId);

}
