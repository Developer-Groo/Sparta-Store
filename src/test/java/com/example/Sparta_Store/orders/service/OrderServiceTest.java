package com.example.Sparta_Store.orders.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.service.CartRedisService;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.CreateOrderRequestDto;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.exception.OrdersErrorCode;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private OrderItemRepository orderItemRepository;

    private Users user;
    private Cart cart;
    private CartItem cartItem;
    private List<CartItem> cartItemList;
    private Address address;
    private Item item;
    private long totalPrice;

    @BeforeEach
    void setUp() {
        address = new Address("경기도", "테스트길", "12345");
        user = new Users(1L, UUID.randomUUID().toString(), "테스트유저", "email@test.com", "Pw1234!!!", address, false, null, null, UserRoleEnum.USER);
        item = new Item(1L, "상품1", "img1@test.com", 10000, "상품1입니다.", 100, null, null);
        cartItemList = new ArrayList<>();
        cart = new Cart(1L, user, cartItemList);
        cartItem = new CartItem(1L, cart, item, 2);
        cartItemList.add(cartItem);

    }

    @Test
    @DisplayName("주문 생성 성공 - 기본 배송지")
    void createRedisOrder_defaultAddress_success() {
        // given
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice);
        given(ordersRepository.save(any(Orders.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        CreateOrderRequestDto requestDto = null;

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(user.getAddress(), order.getAddress());
        assertEquals(order.getOrderStatus(), OrderStatus.BEFORE_PAYMENT);
    }

    @Test
    @DisplayName("주문 생성 성공 - 신규 배송지")
    void createRedisOrder_newAddress_success() {
        // given
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(cartItemList);
        given(cartRedisService.getTotalPrice(cartItemList))
            .willReturn(totalPrice);
        given(ordersRepository.save(any(Orders.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto(new Address("서울시", "테스트길", "12121"));

        // when
        Orders order = orderService.createRedisOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(requestDto.address(), order.getAddress());
        assertEquals(order.getOrderStatus(), OrderStatus.BEFORE_PAYMENT);
    }

    @Test
    @DisplayName("주문 생성 실패 - 장바구니에 담긴 상품 없음")
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
    @DisplayName("주문 아이템 생성 성공")
    void createRedisOrderItem_success() {
        // given
        Orders order = new Orders(user, 50000L, user.getAddress());

        // when
        orderService.createRedisOrderItem(order, cartItemList);

        // then
        // cartItemList의 각 항목에 대해 OrderItem이 하나씩 생성되어야 함
        verify(orderItemRepository, times(cartItemList.size())).saveAll(anyList());
    }

    @Test
    @DisplayName("주문 아이템 생성 실패 - 장바구니에 담긴 상품 없음")
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
        Orders order = new Orders(UUID.randomUUID().toString(), user, OrderStatus.ORDER_COMPLETED, 30000L, address);
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
