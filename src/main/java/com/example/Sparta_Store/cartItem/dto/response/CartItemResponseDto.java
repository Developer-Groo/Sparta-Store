package com.example.Sparta_Store.cartItem.dto.response;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartItemResponseDto {

    private final Long cartItemId;
    private final Long itemId;
    private final Integer quantity;

    public CartItemResponseDto(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.itemId = cartItem.getItem().getId();
        this.quantity = cartItem.getQuantity();
    }

}
