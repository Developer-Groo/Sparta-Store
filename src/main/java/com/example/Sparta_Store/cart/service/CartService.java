package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.exception.CartErrorCode;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * 장바구니 생성
     */
    @Transactional
    public CartResponseDto cartAddition(CartRequestDto responseDto) {

        //TODO userId값 임시고정 추후 삭제예정!!
        Long userId = 1L;

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));

        Item item = itemRepository.findById(responseDto.itemId()).orElseThrow(() -> new CustomException(CartErrorCode.PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findById(user.getId()).orElseGet(() -> cartRepository.save(new Cart(user)));

        // 상품 있는지 확인 (중복방지)
        List<CartItem> cartItems = cartItemRepository.findByCartAndItem(cart, item);

        CartItem cartItem;

        if (!cartItems.isEmpty()) {
            cartItem = cartItems.get(0);
            // 기존 상품이 있으면 수량만 증가
            cartItem.updateQuantity(cartItem.getQuantity() + responseDto.quantity());

            if (cartItems.size() > 1) {
                cartItemRepository.deleteAll(cartItems.subList(1, cartItems.size()));
            }
        } else {
            // 상품 추가
            cartItem = new CartItem(cart, item, responseDto.quantity());
            cartItemRepository.save(cartItem);
        }

        List<CartItem> updateCartItems = cartItemRepository.findByCart(cart, Pageable.unpaged()).getContent();

        return CartResponseDto.toDto(cart, updateCartItems);

    }

    /**
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));

        Page<CartItem> cartItems = cartItemRepository.findByCart(cart, pageable);

        return CartResponseDto.toDto(cart, cartItems);
    }

    /**
     * 장바구니 상품 삭제
     */
    @Transactional
    public void cartItemRemove(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT));

        cartItemRepository.delete(cartItem);
    }

    /**
     * 장바구니 상품 수량 변경
     */
    @Transactional
    public CartItemResponseDto cartItemUpdate(Long cartItemId, CartItemUpdateRequestDto requestDto) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT));

        if (requestDto.quantity() < 1) {
            throw new CustomException(CartErrorCode.PRODUCT_QUANTITY_TOO_LOW);
        }

        cartItem.updateQuantity(requestDto.quantity());

        return CartItemResponseDto.toDto(cartItem);
    }


    // 카트 초기화
    @Transactional
    public void deleteCartItem(List<CartItem> cartItemList) {

        cartItemList.stream()
                .forEach(cartItem -> cartItemRepository.delete(cartItem));
    }
}
