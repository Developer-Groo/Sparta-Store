package com.example.Sparta_Store.cartItem.repository;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.item.entity.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // cart_id 컬럼 값이 cartId인 모든 CartItem 조회
    Optional<List<CartItem>> findByCartId(Long cartId);

    // cart_item 테이블의 cart_id 컬럼 값이 cartId 인 모든 item을 가져온다.
    @Query("SELECT ci.item FROM CartItem ci WHERE ci.cart.id = :cartId")
    Optional<List<Item>> findItemsByCartId(@Param("cardId") Long cartId);

}
