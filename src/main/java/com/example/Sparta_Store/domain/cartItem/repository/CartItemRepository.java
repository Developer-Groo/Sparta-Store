package com.example.Sparta_Store.domain.cartItem.repository;

import com.example.Sparta_Store.domain.cart.entity.Cart;
import com.example.Sparta_Store.domain.cartItem.entity.CartItem;
import java.util.List;
import java.util.Optional;

import com.example.Sparta_Store.domain.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // cart_id 컬럼 값이 cartId인 모든 CartItem 조회
    Optional<List<CartItem>> findByCartId(Long cartId);

    List<CartItem> findByCartAndItem(Cart cart, Item item);

    Page<CartItem> findByCart(Cart cart, Pageable pageable);

}
