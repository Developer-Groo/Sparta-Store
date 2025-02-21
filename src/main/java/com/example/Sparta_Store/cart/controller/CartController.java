package com.example.Sparta_Store.cart.controller;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.service.CartRedisService;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartRedisService cartRedisService;

    /**
     * 장바구니 담기
     */
    @PostMapping
    public ResponseEntity<CartResponseDto> cartAddition(@RequestBody CartRequestDto requestDto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        CartResponseDto cart = cartRedisService.cartAddition(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cart);
    }

    /**
     * 장바구니 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        CartResponseDto cartResponse = cartRedisService.getCart(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartResponse);
    }

    /**
     * 장바구니 상품 삭제
     */
    @PostMapping("/items/{cartItemId}")
    public ResponseEntity<String> cartItemRemove(@PathVariable("cartItemId") Long cartItemId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        cartRedisService.cartItemRemove(cartItemId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("장바구니에 상품이 삭제 되었습니다.");
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> cartItemUpdate(@PathVariable("cartItemId") Long cartItemId, @RequestBody CartItemUpdateRequestDto requestDto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("id");
        CartItemResponseDto updateCart = cartRedisService.cartItemUpdate(cartItemId, requestDto, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updateCart);
    }
}
