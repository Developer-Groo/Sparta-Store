package com.example.Sparta_Store.cart.dto.response;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import org.springframework.data.domain.Page;


public record CartResponseDto(
        Long cartId,
        Long userId,
        Page<CartItemResponseDto> cartItems
) {
    // 장바구니 조회 시 사용
    public static CartResponseDto toDto(Cart cart, Page<CartItem> cartItems) {
        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                cartItems.map(CartItemResponseDto::new)
        );
    }

    // 장바구니 생성 시 사용
    public static CartResponseDto toDto(Cart cart) {
        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                Page.empty()
        );
    }

}
