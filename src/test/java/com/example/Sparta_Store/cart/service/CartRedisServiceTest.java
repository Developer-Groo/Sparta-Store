package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.domain.cart.dto.request.CartRequestDto;
import com.example.Sparta_Store.domain.cart.dto.response.CartResponseDto;
import com.example.Sparta_Store.domain.cart.entity.Cart;
import com.example.Sparta_Store.domain.cart.exception.CartErrorCode;
import com.example.Sparta_Store.domain.cartItem.dto.request.CartItemUpdateRequestDto;
import com.example.Sparta_Store.domain.cartItem.entity.CartItem;
import com.example.Sparta_Store.domain.cart.service.CartRedisService;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.redis.RedisService;
import com.example.Sparta_Store.domain.user.entity.Users;
import com.example.Sparta_Store.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

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

    private Users user;
    private Item item;
    private Cart cart;
    private CartItem cartItem;
    private Map<Long, CartItem> cartItems;

    @BeforeEach
    void setUp() {
        user = new Users("test@test.kr", "password", "테스트", null, null);
        item = new Item(1L, "상품1", "img.jpa", 1000, null, null, null, null);
        cartItems = new HashMap<>();
        cart = new Cart(1L, user, cartItems);
        cartItem = new CartItem(1L, cart, item, 2);
        cartItems.put(item.getId(), cartItem);
    }

    @Test
    @DisplayName("장바구니 생성 성공")
    void cartAddition() {
        // given
        CartRequestDto requestDto = new CartRequestDto(1L, 1L, 1);
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findById(1L))
                .willReturn(Optional.of(item));
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(cart);
        given(redisService.getList("cartItems:" + cart.getId(), CartItem.class))
                .willReturn(new ArrayList<>(List.of(cartItem)));
        // when
        CartResponseDto response = cartRedisService.cartAddition(requestDto, 1L);
        // then
        assertThat(response).isNotNull();
        assertThat(response.cartItemList()).hasSize(1);
        assertThat(response.cartItemList().get(0).quantity()).isEqualTo(3);
        then(redisService).should(times(1)).putObject(anyString(), any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 생성 실패 - 존재하지않는 상품")
    void cartAddError() {
        // given
        CartRequestDto requestDto = new CartRequestDto(1L, 1L, 2);
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findById(1L))
                .willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> cartRedisService.cartAddition(requestDto, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void shoppingCartList() {
        // given
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(cart);
        given(redisService.getList("cartItems:" + cart.getId(), CartItem.class))
                .willReturn(new ArrayList<>(cart.getCartItems()));
        // when
        CartResponseDto response = cartRedisService.shoppingCartList(1L);
        // then
        assertThat(response).isNotNull();
        assertThat(cart.getId()).isEqualTo(response.cartId());
        assertThat(response.cartItemList()).hasSize(cart.getCartItems().size());
    }

    @Test
    @DisplayName("장바구니 조회 실패 - 존재하지않은 장바구니")
    void shoppingCartList_Fall_NotFound() {
        // given
        Cart emptyCart = new Cart(null, user, new HashMap<>());
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(emptyCart);
        // when & then
        assertThatThrownBy(() -> cartRedisService.shoppingCartList(1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.NOT_EXISTS_USER);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공")
    void cartItemRemove() {
        // given
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(cart);
        given(redisService.getList("cartItems:" + cart.getId(), CartItem.class))
                .willReturn(new ArrayList<>(List.of(cartItem)));
        // when
        cartRedisService.cartItemRemove(1L, 1L);
        // then
        then(redisService).should(times(1)).removeFromList(anyString(), eq(cartItem));
        then(redisService).should(times(1)).putObject(anyString(), eq(cart));
    }

    @Test
    @DisplayName("장바구니 상품 삭제 실패 - 장바구니가 없음")
    void cartItemRemove_Fall_NotFound() {
        // given
        Cart emptyCart = new Cart(null, user, new HashMap<>());
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(emptyCart);
        // when & then
        assertThatThrownBy(() -> cartRedisService.cartItemRemove(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
    }

    @Test
    @DisplayName("상품 수량 변경 성공")
    void cartItemUpdate() {
        // given
        CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto(1L, 3);
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(cart);
        given(redisService.getList("cartItems:" + cart.getId(), CartItem.class))
                .willReturn(new ArrayList<>(List.of(cartItem)));
        // when
        cartRedisService.cartItemUpdate(1L, requestDto, 1L);
        // then
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        then(redisService).should(times(1))
                .updateListElement(anyString(), anyInt(), eq(cartItem));
    }

    @Test
    @DisplayName("상품 수량 변경 실패 - 상품 없음")
    void cartItemUpdate_Fall_NotFound() {
        // given
        CartItemUpdateRequestDto requestDto = new CartItemUpdateRequestDto(1L, 3);
        given(redisService.getObject(anyString(), eq(Cart.class)))
                .willReturn(cart);
        given(redisService.getList("cartItems:" + cart.getId(), CartItem.class))
                .willReturn(new ArrayList<>());
        // when & then
        assertThatThrownBy(() -> cartRedisService.cartItemUpdate(1L, requestDto, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.NOT_EXISTS_CART_PRODUCT);
    }
}
