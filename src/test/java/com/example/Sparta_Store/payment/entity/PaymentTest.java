package com.example.Sparta_Store.payment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.example.Sparta_Store.domain.orders.entity.Orders;
import com.example.Sparta_Store.domain.payment.entity.Payment;
import com.example.Sparta_Store.domain.users.entity.Users;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {

    private Users user;
    private Orders order;

    @BeforeEach
    void setUp() {
        user = new Users("테스트", "email@test.com");
        order = Orders.createOrderWithoutCoupon(user, 100000L, null);
    }

    @Test
    @DisplayName("결제 생성 성공")
    void createPayment_success() {
        // given & when
        Payment payment = Payment.toEntity("paymentKey", order, 100000L);

        // then
        assertThat(payment.getOrder().equals(order));
        assertThat(payment.getOrder().getUser().getName().equals("테스트"));
        assertThat(payment.getApprovedAt() == null);
        assertThat(payment.getMethod() == null);
        assertThat(payment.getAmount() == 100000L);
        assertEquals(payment.isCancelled(), false);
        assertEquals(payment.isAborted(), false);

    }

    @Test
    @DisplayName("결제 승인 업데이트 성공")
    void approvedPayment_success() {
        // given
        Payment payment = Payment.toEntity("paymentKey", order, 100000L);
        String date = "2025-02-24T21:11:59.000000+09:00";
        String method = "카드";

        // when
        payment.approvedPayment(date, method);

        // then
        LocalDateTime expectedApprovedAt = OffsetDateTime.parse(date).toLocalDateTime();
        assertEquals(expectedApprovedAt, payment.getApprovedAt());
        assertEquals(method, payment.getMethod());
    }

    @Test
    @DisplayName("결제 승인 거절 업데이트 성공")
    void updateAborted_success() {
        // given
        Payment payment = Payment.toEntity("paymentKey", order, 100000L);

        // when
        payment.updateAborted();

        // then
        assertEquals(payment.isAborted(), true);
    }

    @Test
    @DisplayName("결제 취소 업데이트 성공")
    void updateCancelled_success() {
        // given
        Payment payment = Payment.toEntity("paymentKey", order, 100000L);

        // when
        payment.updateCancelled();

        // then
        assertEquals(payment.isCancelled(), true);
    }

}
