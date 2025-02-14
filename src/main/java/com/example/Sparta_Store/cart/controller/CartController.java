package com.example.Sparta_Store.cart.controller;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.service.CartService;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.util.PageQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기
     */
    @PostMapping
    public ResponseEntity<CartResponseDto> cartAddition(@RequestBody CartRequestDto requestDto) {
        CartResponseDto cart =  cartService.cartAddition(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cart);
    }

    /**
     * 장바구니 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable("userId") Long userId, PageQuery pageQuery) {
        CartResponseDto cartResponse = cartService.getCart(userId, pageQuery.toPageable());
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartResponse);
    }
    /**
     * 장바구니 상품 삭제
     */
    @PostMapping("/items/{cartItemId}")
    public ResponseEntity<String> cartItemRemove(@PathVariable("cartItemId") Long cartItemId) {
        cartService.cartItemRemove(cartItemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("장바구니에 상품이 삭제 되었습니다.");
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> cartItemUpdate(@PathVariable("cartItemId") Long cartItemId, @RequestBody CartItemUpdateRequestDto requestDto) {
        CartItemResponseDto updateCart = cartService.cartItemUpdate(cartItemId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateCart);
    }
}
