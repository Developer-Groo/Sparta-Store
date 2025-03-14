package com.example.Sparta_Store.orders.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.Sparta_Store.domain.IssuedCoupon.entity.IssuedCoupon;
import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.user.service.UserRoleEnum;
import com.example.Sparta_Store.domain.orders.OrderStatus;
import com.example.Sparta_Store.domain.orders.entity.Orders;
import com.example.Sparta_Store.domain.user.entity.Users;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    private Users user;
    private Address address;
    private IssuedCoupon issuedCoupon;

    @BeforeEach
    void setUp() {
        issuedCoupon = new IssuedCoupon(1L, "randomCoupon", 1000L, 1L, false, null);
        address = new Address("경기도", "테스트길", "12345");
        user = new Users(1L, UUID.randomUUID().toString(), "테스트유저", "email@test.com", "Pw1234!!!", address, false, null, null, UserRoleEnum.USER);
    }

    @Test
    @DisplayName("주문 생성 성공 - order_status 기본값은 BEFORE_PAYMENT")
    void createOrder_Default_Order_Status() {
        // given & when
        Orders order = Orders.createOrderWithoutCoupon(user, 30000, user.getAddress());

        // then
        assertThat(order.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT));
    }

    @Test
    @DisplayName("쿠폰이 적용된 주문 생성 성공")
    void createOrder_issuedCoupon() {
        // given & when
        Orders order = Orders.createOrderWithCoupon(user, 30000, user.getAddress(), issuedCoupon);

        // then
        assertThat(order.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT));
    }

    @Test
    @DisplayName("주문상태 변경 성공")
    void updateOrderStatus() {
        // given
        Orders order = Orders.createOrderWithoutCoupon(user, 30000, user.getAddress());

        // when
        order.updateOrderStatus(OrderStatus.CONFIRMED);

        // then
        assertThat(order.getOrderStatus().equals(OrderStatus.CONFIRMED));
    }

}
