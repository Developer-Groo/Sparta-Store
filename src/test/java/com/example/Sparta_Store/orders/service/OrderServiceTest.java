package com.example.Sparta_Store.orders.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;
import com.example.Sparta_Store.IssuedCoupon.repository.IssuedCouponRepository;
import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.service.CartRedisService;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.CreateOrderRequestDto;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.exception.OrdersErrorCode;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRedisService cartRedisService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    private Users user;
    private Cart cart;
    private CartItem cartItem;
    private List<CartItem> cartItemList;
    private Address address;
    private Item item;
    private long totalPrice;
    private IssuedCoupon issuedCoupon;

    @BeforeEach
    void setUp() {
        issuedCoupon = new IssuedCoupon(1L, "randomCoupon", "1000", 1L, false, null);
        address = new Address("경기도", "테스트길", "12345");
        user = new Users(1L, UUID.randomUUID().toString(), "테스트유저", "email@test.com", "Pw1234!!!", address, false, null, null, UserRoleEnum.USER);
        item = new Item(1L, "상품1", "img1@test.com", 10000, "상품1입니다.", 100, null, null);
        cartItemList = new ArrayList<>();
        cart = new Cart(1L, user, cartItemList);
        cartItem = new CartItem(1L, cart, item, 2);
        cartItemList.add(cartItem);
        totalPrice = 20000L;
    }

    @Test
    @DisplayName("레디스 주문 생성 성공 - 기본 배송지")
    void createRedisOrder_defaultAddress_success() throws JsonProcessingException {
        // given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice);
        given(objectMapper.writeValueAsString(any(Orders.class))).willReturn("orderJson");

        CreateOrderRequestDto requestDto = null;

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(user.getAddress(), order.getAddress());
        assertEquals(Long.valueOf(order.getTotalPrice()), Long.valueOf(totalPrice));
        assertEquals(OrderStatus.BEFORE_PAYMENT, order.getOrderStatus());

        verify(hashOperations, times(1)).put(anyString(), eq("order"), eq("orderJson"));
        verify(hashOperations, times(1)).put(anyString(), eq("createdAt"), anyString());
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("레디스 주문 생성 성공 - 신규 배송지")
    void createRedisOrder_newAddress_success() throws JsonProcessingException {
        // given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice);
        given(objectMapper.writeValueAsString(any(Orders.class))).willReturn("orderJson");

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(new Address("서울시", "테스트길", "12121"), null);

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(requestDto.address(), order.getAddress());
        assertEquals(Long.valueOf(order.getTotalPrice()), Long.valueOf(totalPrice));
        assertEquals(OrderStatus.BEFORE_PAYMENT, order.getOrderStatus());

        verify(hashOperations, times(1)).put(anyString(), eq("order"), eq("orderJson"));
        verify(hashOperations, times(1)).put(anyString(), eq("createdAt"), anyString());
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("레디스 주문 생성 성공 - 쿠폰 적용")
    void createRedisOrder_use_coupon_success() throws JsonProcessingException {
        // given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice);
        given(objectMapper.writeValueAsString(any(Orders.class))).willReturn("orderJson");
        given(issuedCouponRepository.couponToUse(1L, 1L))
            .willReturn(issuedCoupon);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(new Address("서울시", "테스트길", "12121"), 1L);

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals((long) order.getTotalPrice(), totalPrice-Long.parseLong(issuedCoupon.getAmount()));
        assertEquals(OrderStatus.BEFORE_PAYMENT, order.getOrderStatus());

        verify(hashOperations, times(1)).put(anyString(), eq("order"), eq("orderJson"));
        verify(hashOperations, times(1)).put(anyString(), eq("createdAt"), anyString());
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("레디스 주문 생성 성공 - 쿠폰 적용 후 최소주문금액 100원")
    void createRedisOrder_use_coupon2_success() throws JsonProcessingException {
        // given
        given(redisTemplate.opsForHash()).willReturn(hashOperations);
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice=1000L);
        given(objectMapper.writeValueAsString(any(Orders.class))).willReturn("orderJson");
        given(issuedCouponRepository.couponToUse(1L, 1L))
            .willReturn(issuedCoupon);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(new Address("서울시", "테스트길", "12121"), 1L);

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals((long) order.getTotalPrice(), 100);
        assertEquals(OrderStatus.BEFORE_PAYMENT, order.getOrderStatus());

        verify(hashOperations, times(1)).put(anyString(), eq("order"), eq("orderJson"));
        verify(hashOperations, times(1)).put(anyString(), eq("createdAt"), anyString());
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("레디스 주문 생성 실패 - 장바구니에 담긴 상품 없음")
    void createRedisOrder_notExistsCartItem_fail() {
        // given
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(new ArrayList<>());

        CreateOrderRequestDto requestDto = null;

        // when & then
        assertThatThrownBy(() -> orderService.createRedisOrder(user.getId(), requestDto))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.NOT_EXISTS_CART_PRODUCT);
    }

    @Test
    @DisplayName("MySQL 결제 완료된 주문 생성 성공")
    void createMysqlOrder_success() {
        // given
        Orders order = new Orders(user, 50000L, user.getAddress());

        // when
        orderService.createMysqlOrder(order);

        // then
        verify(ordersRepository, times(1)).save(eq(order));
    }

    @Test
    @DisplayName("레디스 주문 아이템 생성 성공")
    void createRedisOrderItem_success() throws JsonProcessingException {
        // given
        given(redisTemplate.opsForList()).willReturn(listOperations);
        Orders order = new Orders(user, 10000L, user.getAddress());

        doAnswer(invocation -> {
            OrderItem orderItem = invocation.getArgument(0);
            return new ObjectMapper().writeValueAsString(orderItem);
        }).when(objectMapper).writeValueAsString(any(OrderItem.class));

        // when
        orderService.createRedisOrderItem(order, cartItemList);

        // then
        verify(listOperations, times(cartItemList.size())).rightPush(anyString(), anyString());
        verify(redisTemplate, times(1)).expire(anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("레디스 주문 아이템 생성 실패 - 장바구니에 담긴 상품 없음")
    void createRedisOrderItem_notExistsCartItem_fail() {
        // given
        Orders order = new Orders(user, 50000L, user.getAddress());
        List<CartItem> cartItems = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> orderService.createRedisOrderItem(order, cartItems))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.NOT_EXISTS_CART_PRODUCT);
    }

    @Test
    @DisplayName("주문 상태 변경 가능 여부 확인 - 가능")
    void isStatusUpdatable_success() {
        // given
        OrderStatus originStatus = OrderStatus.DELIVERED;
        OrderStatus requestStatus = OrderStatus.CONFIRMED;

        // when & then
        assertDoesNotThrow(() ->
            orderService.isStatusUpdatable(originStatus, requestStatus)
        );
    }

    @Test
    @DisplayName("주문 상태 변경 가능 여부 확인 - 불가능")
    void isStatusUpdatable_fail() {
        // given
        OrderStatus originStatus = OrderStatus.DELIVERED;
        OrderStatus requestStatus = OrderStatus.ORDER_CANCEL_REQUEST;

        // when & then
        assertThatThrownBy(() -> orderService.isStatusUpdatable(originStatus, requestStatus))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.ORDER_STATUS_INVALID_TRANSITION);
    }


    @Test
    @DisplayName("주문 상태 변경 성공")
    void updateOrderStatus_success() {
        // given
        Orders order = new Orders(UUID.randomUUID().toString(), user, OrderStatus.ORDER_COMPLETED, 30000L, address, null);
        given(ordersRepository.findById(order.getId())).willReturn(Optional.of(order));

        UpdateOrderStatusDto requestDto = new UpdateOrderStatusDto("ORDER_CANCEL_REQUEST");

        // when
        orderService.updateOrderStatus(user.getId(), order.getId(), requestDto);

        // then
        assertEquals(OrderStatus.ORDER_CANCEL_REQUEST, order.getOrderStatus());
    }

    @Test
    @DisplayName("주문 상태 변경 실패 - 주문 정보 없음")
    void updateOrderStatus_fail() {
        // given
        given(ordersRepository.findById(anyString())).willReturn(Optional.empty());

        UpdateOrderStatusDto requestDto = new UpdateOrderStatusDto("RETURN_REQUESTED");

        // when & then
        assertThatThrownBy(() -> orderService.updateOrderStatus(user.getId(), UUID.randomUUID().toString(), requestDto))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.NOT_EXISTS_ORDER);
    }

}
