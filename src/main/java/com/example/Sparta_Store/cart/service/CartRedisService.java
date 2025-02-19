package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.exception.CartErrorCode;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.dto.response.CartItemResponseDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private HashOperations<String, String, Cart> cartHashOperations;
    private HashOperations<String, String, CartItem> cartItemHashOperations;
    private ZSetOperations<String, Object> zSetOperations;

    @PostConstruct
    private void init() {
        cartHashOperations = redisTemplate.opsForHash();
        cartItemHashOperations = redisTemplate.opsForHash();
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    private String getCartKey(Long userId) {
        return "cart:" + userId;
    }

    private String getCartItemKey(Long cartId) {
        return "cartItem:" + cartId;
    }

    @Transactional
    public CartResponseDto cartAddition(CartRequestDto responseDto) {
        Long userId = 1L;
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findById(responseDto.itemId()).orElseThrow(() -> new CustomException(CartErrorCode.PRODUCT_NOT_FOUND));

        String cartKey = getCartKey(userId);
        Cart cart = Optional.ofNullable(cartHashOperations.get(cartKey, "cart")).orElse(new Cart(user));

        String cartItemKey = getCartItemKey(cart.getId());
        CartItem cartItem = Optional.ofNullable(cartItemHashOperations.get(cartItemKey, String.valueOf(item.getId())))
                .orElse(new CartItem(cart, item, 0));

        cartItem.updateQuantity(cartItem.getQuantity() + responseDto.quantity());
        cartItemHashOperations.put(cartItemKey, String.valueOf(item.getId()), cartItem);
        cartHashOperations.put(cartKey, "cart", cart);

        // 상품 1일 뒤 자동 삭제
        redisTemplate.expire(cartItemKey, Duration.ofDays(1));

        return CartResponseDto.toDto(cart, List.of(cartItem));

    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long userId, Pageable pageable) {
        String cartKey = getCartKey(userId);

        Cart cart = Optional.ofNullable(cartHashOperations.get(cartKey, "cart")).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));
        String cartItemKey = getCartItemKey(cart.getId());

        long start = pageable.getPageNumber() * pageable.getPageSize();
        long end = start + pageable.getPageSize() -1;

        Set<Object> itemIds = zSetOperations.range(cartItemKey, start, end);
        List<CartItem> cartItems = itemIds.stream()
                        .map(id -> cartItemHashOperations.get(cartItemKey, String.valueOf(id)))
                .filter(Objects::nonNull)
                .map(obj -> (CartItem) obj)
                .toList();


        return CartResponseDto.toDto(cart, cartItems);
    }


    @Transactional
    public void cartItemRemove(Long cartItemId, Long userId) {
        String cartKey = getCartKey(userId);

        Cart cart = Optional.ofNullable(cartHashOperations.get(cartKey, "cart")).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT));
        String cartItemKey = getCartItemKey(cart.getId());

        cartItemHashOperations.delete(cartItemKey, String.valueOf(cartItemId));

    }

    // 상품 수량 변경
    @Transactional
    public CartItemResponseDto cartItemUpdate(Long cartItemId, CartItemUpdateRequestDto requestDto, Long userId) {
        String cartKey = getCartKey(userId);
        Cart cart = Optional.ofNullable(cartHashOperations.get(cartKey, "cart")).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT));

        String cartItemKey = getCartItemKey(cart.getId());
        CartItem cartItem = Optional.ofNullable(cartItemHashOperations.get(cartItemKey, String.valueOf(cartItemId))).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT));

        if (requestDto.quantity() < 1) {
            throw new CustomException(CartErrorCode.PRODUCT_QUANTITY_TOO_LOW);
        }
        cartItem.updateQuantity(requestDto.quantity());
        cartItemHashOperations.put(cartItemKey, String.valueOf(cartItemId), cartItem);

        return CartItemResponseDto.toDto(cartItem);
    }


}
