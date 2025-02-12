package com.example.Sparta_Store.cartItem.dto.response;

import com.example.Sparta_Store.cartItem.entity.CartItem;

public record CartItemResponseDto(
        Long cartItemId,
        Long itemId,
        Integer quantity
) {
    public CartItemResponseDto(CartItem cartItem) {
        this(cartItem.getId(),
        cartItem.getItem().getId(),
        cartItem.getQuantity());

    }

}
