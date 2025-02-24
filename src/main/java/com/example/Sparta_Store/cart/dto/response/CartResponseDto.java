package com.example.Sparta_Store.cart.dto.response;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;

import java.util.List;

public record CartResponseDto(
        Long cartId,
        Long userId,
        @JsonIgnore
        Page<CartItemResponseDto> cartItems,
        List<CartItemResponseDto> cartItemList

) {

    // 장바구니 조회 시 사용
    public static CartResponseDto toDto(Cart cart, Page<CartItem> cartItems) {
        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                cartItems.map(CartItemResponseDto::toDto),
                null
        );
    }

    // 장바구니 생성 시 사용
    public static CartResponseDto toDto(Cart cart, List<CartItem> cartItemList) {
        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                null,
                cartItemList.stream().map(CartItemResponseDto::toDto).toList()
        );
    }

}
