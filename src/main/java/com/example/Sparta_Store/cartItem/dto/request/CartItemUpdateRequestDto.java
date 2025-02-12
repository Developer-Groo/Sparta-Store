package com.example.Sparta_Store.cartItem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class CartItemUpdateRequestDto {

    private final Long cartItemId;
    private final Integer quantity;

}
