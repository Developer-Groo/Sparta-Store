package com.example.Sparta_Store.cart.controller;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<CartResponseDto> cartAddition(@RequestBody CartRequestDto requestDto) {
        CartResponseDto cart =  cartService.cartAddition(requestDto);
        return ResponseEntity.ok(cart);
    }

    /**
     * 장바구니 조회
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable Long userId) {
        CartResponseDto cartResponse = cartService.getCart(userId);
        return ResponseEntity.ok(cartResponse);
    }

}
