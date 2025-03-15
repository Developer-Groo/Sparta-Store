package com.example.Sparta_Store.domain.cartItem.dto.response;

import com.example.Sparta_Store.domain.cartItem.entity.CartItem;

public record CartItemResponseDto(
        Long cartItemId,
        Long itemId,
        String itemName,
        Integer quantity,
        Integer price
) {
    public static CartItemResponseDto toDto(CartItem cartItem) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getItem().getId(),
                cartItem.getItem().getName(),
                cartItem.getQuantity(),
                cartItem.getItem().getPrice()
        );
    }
}
