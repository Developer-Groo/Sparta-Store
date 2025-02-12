package com.example.Sparta_Store.cartItem.service;

import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    /**
     * 장바구니 상품 삭제
     * @param cartItemId
     */
    @Transactional
    public void cartItemRemove(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(()-> new IllegalArgumentException("해당 장바구니 상품이 존재하지 않습니다."));

        cartItemRepository.delete(cartItem);
    }

    /**
     * 장바구니 상품 수량 변경
     * @param requestDto
     * @return
     */
    @Transactional
    public CartItemResponseDto cartItemUpdate(CartItemUpdateRequestDto requestDto) {

        CartItem cartItem = cartItemRepository.findById(requestDto.getCartItemId()).orElseThrow(()-> new IllegalArgumentException("해당 장바구니 상품이 존재하지 않습니다."));

        if(requestDto.getQuantity() < 1) {
            throw new IllegalArgumentException("상품 수량은 1 이상이어야 합니다.");
        }

        cartItem.updateQuantity(requestDto.getQuantity());

        return new CartItemResponseDto(cartItem);
    }
}
