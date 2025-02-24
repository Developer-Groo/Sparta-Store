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
import com.example.Sparta_Store.redis.RedisService;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RedisService redisService;
    private final ZSetOperations<String, Object> zSetOperations;


    private String getCartKey(Long userId) {
        return "cart:" + userId;
    }

    private String getCartItemKey(Long cartId) {
        return "cartItem:" + cartId;
    }

    @Transactional
    public CartResponseDto cartAddition(CartRequestDto responseDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findById(responseDto.itemId()).orElseThrow(() -> new CustomException(CartErrorCode.PRODUCT_NOT_FOUND));

        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);
        if (cart.getId() == null) {
           cart = cartRepository.saveAndFlush(new Cart(user));
        }

        String cartItemKey = getCartItemKey(cart.getId());
        CartItem cartItem = redisService.getObject(cartItemKey, CartItem.class);
        if (cartItem.getId() == null) {
           cartItem = cartItemRepository.saveAndFlush(new CartItem(cart, item, 0));
        }

        cartItem.updateQuantity(cartItem.getQuantity() + responseDto.quantity());

        cart.addCartItem(cartItem);
        redisService.putObject(cartItemKey, cartItem);
        redisService.putObject(cartKey, cart);

        // 상품 1일 뒤 자동 삭제
        redisTemplate.expire(cartItemKey, Duration.ofDays(1));

        return CartResponseDto.toDto(cart, List.of(cartItem));

    }

    public CartResponseDto shoppingCartList(Long userId) {
        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);
        if (cart.getId() == null) {
          throw new CustomException(CartErrorCode.NOT_EXISTS_USER);
        }

        return CartResponseDto.toDto(cart, cart.getCartItems());
    }

    @Transactional
    public void cartItemRemove(Long cartItemId, Long userId) {
        String cartKey = getCartKey(userId);

        log.info("user id {}", userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);

        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }
        String cartItemKey = getCartItemKey(cart.getId());

        redisService.delete(cartItemKey);
    }

    // 상품 수량 변경
    @Transactional
    public CartItemResponseDto cartItemUpdate(Long cartItemId, CartItemUpdateRequestDto requestDto, Long userId) {
        log.info("user Id {}", userId);

        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);

        if (cart.getId() == null) {
            throw  new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        String cartItemKey = getCartItemKey(cart.getId());
        CartItem cartItem = redisService.getObject(cartItemKey, CartItem.class);
        if (cartItem.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        if (requestDto.quantity() < 1) {
            throw new CustomException(CartErrorCode.PRODUCT_QUANTITY_TOO_LOW);
        }
        cartItem.updateQuantity(requestDto.quantity());
        redisService.putObject(cartItemKey, cartItem);

        return CartItemResponseDto.toDto(cartItem);
    }


}
