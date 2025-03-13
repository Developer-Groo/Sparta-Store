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
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    private String getCartKey(Long userId) {
        return "cart:" + userId;
    }

    private String getCartItemKey(Long cartId) {
        return "cartItem:" + cartId;
    }
    private String getCartItemListKey(Long cartId) {
        return "cartItems:" + cartId;
    }

    @Transactional
    public CartResponseDto cartAddition(CartRequestDto requestDto, Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CartErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findById(requestDto.itemId())
                .orElseThrow(() -> new CustomException(CartErrorCode.PRODUCT_NOT_FOUND));

        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);

        if (cart.getId() == null) {
            cart = cartRepository.saveAndFlush(new Cart(user));
        }
        String cartItemListKey = getCartItemListKey(cart.getId());
        // 기존 리스트에서 해당 상품이 이미 등록되어있는지 확인
        List<CartItem> existingItems = redisService.getList(cartItemListKey, CartItem.class);
        CartItem targetItem = null;
        int targetIndex = -1;
        for (int i = 0; i < existingItems.size(); i++) {
            CartItem ci = existingItems.get(i);
            if (ci.getItem().getId().equals(item.getId())) {
                targetItem = ci;
                targetIndex = i;
                break;
            }
        }

        if (targetItem != null) {
            targetItem.updateQuantity(targetItem.getQuantity() + requestDto.quantity());
            redisService.updateListElement(cartItemListKey, targetIndex, targetItem);
        } else {
            CartItem newItem = cartItemRepository.saveAndFlush(new CartItem(cart, item, 0));
            newItem.updateQuantity(newItem.getQuantity() + requestDto.quantity());
            cart.addCartItem(newItem.getItem().getId(), newItem);
            redisService.pushToList(cartItemListKey, newItem);
            targetItem = newItem;
        }

        redisService.putObject(cartKey, cart);

        // 상품 1일 뒤 자동 삭제
        redisTemplate.expire(cartItemListKey, Duration.ofDays(1));

        return CartResponseDto.toDto(cart, List.of(targetItem));
    }

    public CartResponseDto shoppingCartList(Long userId) {
        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);
        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_USER);
        }

        String cartItemListKey = getCartItemListKey(cart.getId());
        List<CartItem> cartItems = redisService.getList(cartItemListKey, CartItem.class);
        return CartResponseDto.toDto(cart, cartItems);
    }

    @Transactional
    public void cartItemRemove(Long cartItemId, Long userId) {
        String cartKey = getCartKey(userId);

        log.info("user id {}", userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);

        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }
        String cartItemListKey = getCartItemListKey(cart.getId());
        List<CartItem> itemList = redisService.getList(cartItemListKey, CartItem.class);
        CartItem targetItem = null;
        for (CartItem ci : itemList) {
            if (ci.getId().equals(cartItemId)) {
                targetItem = ci;
                break;
            }
        }
        if (targetItem != null) {
            redisService.removeFromList(cartItemListKey, targetItem);
            // 또한, 카트 엔티티 내부의 Map에서도 제거
            cart.removeCartItem(targetItem.getItem().getId());
            redisService.putObject(cartKey, cart);
        }

    }

    // 상품 수량 변경
    @Transactional
    public CartItemResponseDto cartItemUpdate(Long cartItemId,
                                              CartItemUpdateRequestDto requestDto,
                                              Long userId) {
        log.info("user Id {}", userId);

        String cartKey = getCartKey(userId);
        Cart cart = redisService.getObject(cartKey, Cart.class);

        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        String cartItemListKey = getCartItemListKey(cart.getId());
        List<CartItem> itemList = redisService.getList(cartItemListKey, CartItem.class);
        CartItem targetItem = null;
        int targetIndex = -1;
        for (int i = 0; i < itemList.size(); i++) {
            CartItem ci = itemList.get(i);
            if (ci.getId().equals(cartItemId)) {
                targetItem = ci;
                targetIndex = i;
                break;
            }
        }

        if (targetItem == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        if (requestDto.quantity() < 1) {
            throw new CustomException(CartErrorCode.PRODUCT_QUANTITY_TOO_LOW);
        }
        targetItem.updateQuantity(targetItem.getQuantity() +  requestDto.quantity());
        redisService.updateListElement(cartItemListKey, targetIndex, targetItem);

        return CartItemResponseDto.toDto(targetItem);
    }

    public List<CartItem> getCartItemList(Long userId) {
        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);
        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_USER);
        }

        return cart.getCartItems().stream().toList();
    }

    @Transactional
    @Retryable(
        value = { CustomException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void deleteCartItem(Long userId) {
        log.info("장바구니 초기화 호출 (userId: {})", userId);

        String cartKey = getCartKey(userId);

        Cart cart = redisService.getObject(cartKey, Cart.class);
        if (cart.getId() == null) {
            throw new CustomException(CartErrorCode.NOT_EXISTS_USER);
        }

        List<CartItem> cartItemList = getCartItemList(userId);
        cartItemList.stream()
                .forEach(cartItem -> redisTemplate.delete(getCartItemKey(cart.getId())));
    }

    // get totalPrice
    public long getTotalPrice(List<CartItem> cartItemList) {
        long totalPrice = 0;

        for (CartItem cartItem : cartItemList) {
            int orderPrice = (cartItem.getItem().getPrice()) * (cartItem.getQuantity());
            totalPrice += orderPrice;
        }

        return totalPrice;
    }

}
