package com.example.Sparta_Store.orders.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.service.CartRedisService;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
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

    @Mock
    private ItemService itemService;

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
    void createOrder_defaultAddress_success() {
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
        Orders order = orderService.createOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(user.getAddress(), order.getAddress());
    }

    @Test
    @DisplayName("주문 생성 성공 - 신규 배송지")
    void createOrder_newAddress_success() {
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
        Orders order = orderService.createOrder(user.getId(), requestDto);

        // then
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(requestDto.address(), order.getAddress());
    }

    @Test
    @DisplayName("주문 생성 실패 - 장바구니에 담긴 상품 없음")
    void createOrder_notExistsCartItem_fail() {
        // given
        given(userRepository.findById(1L))
            .willReturn(Optional.of(user));
        given(cartRedisService.getCartItemList(1L))
            .willReturn(new ArrayList<>());

        CreateOrderRequestDto requestDto = null;

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(user.getId(), requestDto))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.NOT_EXISTS_CART_PRODUCT);
    }

    @Test
    @DisplayName("주문 아이템 생성 성공")
    void createOrderItem_success() {
        // given
        Orders order = new Orders(user, 50000L, user.getAddress());
        given(ordersRepository.save(any(Orders.class)))  // 주문 저장
            .willReturn(order);
        given(orderItemRepository.save(any(OrderItem.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        // when
        orderService.createOrderItem(order, cartItemList);

        // then
        // cartItemList의 각 항목에 대해 OrderItem이 하나씩 생성되어야 함
        then(orderItemRepository).should(times(cartItemList.size())).save(any(OrderItem.class));
        then(ordersRepository).should(times(1)).save(order);
    }

    @Test
    @DisplayName("주문 아이템 생성 실패 - 장바구니에 담긴 상품 없음")
    void createOrderItem_notExistsCartItem_fail() {
        // given
        Orders order = new Orders(user, 50000L, user.getAddress());
        List<CartItem> cartItems = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> orderService.createOrderItem(order, cartItems))
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

    @Test
    @DisplayName("결제 승인 전, 상품 재고 감소 호출 및 주문상태 변경 성공")
    void completeOrder_success() {
        // given
        Orders order = new Orders(UUID.randomUUID().toString(), user, OrderStatus.BEFORE_PAYMENT, 30000L, address);
        OrderItem orderItem = new OrderItem(1L, order, item, 30000, 3);
        List<OrderItem> orderItemList = List.of(orderItem);
        String orderId = order.getId();

        given(ordersRepository.findById(orderId))
            .willReturn(Optional.of(order));
        given(orderItemRepository.findOrderItemsByOrders(order))
            .willReturn(Optional.of(orderItemList));

        // when
        orderService.completeOrder(orderId);

        // then
        then(itemService).should().decreaseStock(orderItemList);
        assertEquals(order.getOrderStatus(), OrderStatus.ORDER_COMPLETED);
    }

}
