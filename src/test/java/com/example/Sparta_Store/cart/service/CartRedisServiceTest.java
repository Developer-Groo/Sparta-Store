package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.redis.RedisService;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CartRedisServiceTest {

    @InjectMocks
    private CartRedisService cartRedisService;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    private User user;
    private Item item;
    private Cart cart;
    private CartItem cartItem;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        user = new User("test@test.kr","password", "테스트", null , null);
        item = new Item(1L, "상품1", "img.jpa", 1000, null,null,
                null, null);
        cartItems = new ArrayList<>();
        cart = new Cart(1L, user, cartItems);
        cartItem = new CartItem(1L, cart, item, 2);
        cartItems.add(cartItem);
    }

    @Test
    @DisplayName("장바구니 생성 성공")
    void cartAddition () {
        // given
        CartRequestDto requestDto = new CartRequestDto(1L, 1L, 1);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(cart);
        log.info("cart {}", cart);
        when(redisService.getObject(anyString(), eq(CartItem.class)))
                .thenReturn(cartItem);
        log.info("cartItem {}", cartItem);
        // when
        CartResponseDto response = cartRedisService.cartAddition(requestDto, 1L);
        // then
        assertNotNull(response);
        assertEquals(1, response.cartItemList().size());
        verify(redisService, times(1)).putObject(anyString(), any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 존재하지않는 상품")
    void cartAddError() {
        // given
        CartRequestDto requestDto = new CartRequestDto(1L, 1L, 2);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        // when & then
        assertThrows(CustomException.class, () -> cartRedisService.cartAddition(requestDto, 1L));

    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void shoppingCartList() {
        // given
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(cart);
        // when
        CartResponseDto response = cartRedisService.shoppingCartList(1L);
        // then
        assertNotNull(response);
        assertEquals(cart.getId(), response.cartId());
        assertEquals(cart.getCartItems().size(), response.cartItemList().size());
    }

    @Test
    @DisplayName("장바구니 조회 실패 - 존재하지않은 장바구니")
    void shoppingCartList_Fall_NotFound() {
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(null);
        assertThrows(CustomException.class, () -> cartRedisService.shoppingCartList(1L));
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공")
    void cartItemRemove() {
        // given
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(cart);
        // when
        cartRedisService.cartItemRemove(1L, 1L);
        // then
        verify(redisService, times(1))
                .delete(anyString());
    }

    @Test
    @DisplayName("장바구니 상품 삭제 실패 - 장바구니가 없음")
    void cartItemRemove_Fall_NotFound() {
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(null);
        assertThrows(CustomException.class, () -> cartRedisService.cartItemRemove(1L, 1L));
    }

    @Test
    @DisplayName("상품 수량 변경 성공")
    void cartItemUpdate() {
        CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto(1L, 3);
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(cart);
        when(redisService.getObject(anyString(), eq(CartItem.class)))
                .thenReturn(cartItem);
        cartRedisService.cartItemUpdate(1L, requestDto, 1L);
        assertEquals(3, cartItem.getQuantity());
        verify(redisService, times(1)).putObject(anyString(), any(CartItem.class));
    }

    @Test
    @DisplayName("상품 수량 변경 실패 - 상품 없음")
    void cartItemUpdate_Fall_NotFound() {
        CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto(1L, 3);
        when(redisService.getObject(anyString(), eq(Cart.class)))
                .thenReturn(cart);
        when(redisService.getObject(anyString(), eq(CartItem.class)))
                .thenReturn(null);
        assertThrows(CustomException.class, () -> cartRedisService.cartItemUpdate(1L, requestDto, 1L));
    }
}
