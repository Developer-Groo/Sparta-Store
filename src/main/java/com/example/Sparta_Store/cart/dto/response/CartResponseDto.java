package com.example.Sparta_Store.cart.dto.response;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@RequiredArgsConstructor
public class CartResponseDto {

    private final Long cartId;
    private final Long userId;
    private final List<CartItemResponseDto> cartItems;

    public static CartResponseDto toDto(Cart cart) {
        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCartItems().stream()
                        .map(CartItemResponseDto::new)
                        .collect(Collectors.toList())
        );

    }

}
