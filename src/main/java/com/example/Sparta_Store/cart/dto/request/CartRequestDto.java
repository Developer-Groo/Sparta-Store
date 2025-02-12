package com.example.Sparta_Store.cart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartRequestDto {

    private final Long itemId;
    private final Integer quantity;
}
