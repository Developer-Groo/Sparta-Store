package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * 장바구니 담기
     */
    @Transactional
    public CartResponseDto cartAddition(CartRequestDto responseDto) {

        //TODO userId값 임시고정 추후 삭제예정!!
        Long userId = 1L;

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Item item = itemRepository.findById(responseDto.itemId()).orElseThrow(()-> new IllegalArgumentException("해당 상품이 없습니다."));

        Cart cart = cartRepository.findById(user.getId()).orElseGet(()-> cartRepository.save(new Cart(user)));

        CartItem cartItem = new CartItem(cart, item, responseDto.quantity());
        cartItemRepository.save(cartItem);

        return CartResponseDto.toDto(cart);

    }

    /**
     * 장바구니 조회
     */
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long userId, Pageable pageQuery) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));

        Page<CartItem> cartItems = cartItemRepository.findByCart(cart, pageQuery);

        return CartResponseDto.toDto(cart, cartItems);
    }
}
