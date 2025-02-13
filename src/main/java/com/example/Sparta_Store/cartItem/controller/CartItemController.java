package com.example.Sparta_Store.cartItem.controller;

import com.example.Sparta_Store.cartItem.dto.request.CartItemRemoveRequestDto;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartItems")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * 장바구니 상품 삭제
     */
    @PostMapping("/remove")
    public ResponseEntity<String> cartItemRemove(@RequestBody CartItemRemoveRequestDto requestDto) {
        cartItemService.cartItemRemove(requestDto.cartItemId());
        return ResponseEntity.status(HttpStatus.OK)
                .body("장바구니에 상품이 삭제 되었습니다.");
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @PatchMapping("/update")
    public ResponseEntity<CartItemResponseDto> cartItemUpdate(@RequestBody CartItemUpdateRequestDto requestDto) {
        CartItemResponseDto updateCart = cartItemService.cartItemUpdate(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateCart);
    }
}
