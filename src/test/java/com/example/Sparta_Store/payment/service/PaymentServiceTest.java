package com.example.Sparta_Store.payment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.example.Sparta_Store.domain.payment.service.PaymentService;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.domain.user.service.UserRoleEnum;
import com.example.Sparta_Store.domain.orders.OrderStatus;
import com.example.Sparta_Store.domain.orders.entity.Orders;
import com.example.Sparta_Store.domain.orders.exception.OrdersErrorCode;
import com.example.Sparta_Store.domain.orders.service.OrderService;
import com.example.Sparta_Store.domain.payment.entity.Payment;
import com.example.Sparta_Store.domain.payment.exception.PaymentErrorCode;
import com.example.Sparta_Store.domain.payment.repository.PaymentRepository;
import com.example.Sparta_Store.domain.user.entity.Users;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    OrderService orderService;

    private Users user;
    private Orders order;

    @BeforeEach
    void setUp() {
        user = new Users(1L, UUID.randomUUID().toString(), "테스트", "email@test.com", "Pw1234!!!",
            null, false, null, null, UserRoleEnum.USER);
        order = new Orders("testOrderId", user, OrderStatus.BEFORE_PAYMENT, 10000L, null, null);
    }

    @Test
    @DisplayName("결제전, 주문상태 확인 성공 - true 케이스")
    void checkBeforePayment_success() {
        // given
        String orderId = "testOrderId";
        OrderStatus orderStatus = OrderStatus.BEFORE_PAYMENT;

        given(orderService.getRedisOrder(orderId)).willReturn(order);

        // when
        boolean result = paymentService.checkBeforePayment(orderId);

        // then
        assertEquals(orderStatus, order.getOrderStatus());
        assertTrue(result);
    }

    @Test
    @DisplayName("결제전, 주문상태 확인 성공 - false, 주문상태가 'BEFORE_PAYMENT'이 아닌 케이스")
    void checkBeforePayment_otherStatus_success() {
        // given
        order.updateOrderStatus(OrderStatus.PAYMENT_CANCELLED);
        given(orderService.getRedisOrder(order.getId())).willReturn(order);

        // when
        boolean result = paymentService.checkBeforePayment("testOrderId");

        // then
        assertNotEquals(order.getOrderStatus(), OrderStatus.BEFORE_PAYMENT);
        assertFalse(result);
    }

    @Test
    @DisplayName("결제전, 주문상태 확인 실패 - 주문 정보 없음")
    void testGetOrder_withEmptyOptional() {
        // given
        String orderId = "testOrderId";
        given(orderService.getRedisOrder(orderId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> paymentService.checkBeforePayment(orderId))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.NOT_EXISTS_ORDER);  // 예외 코드 확인
    }

    @Test
    @DisplayName("결제 승인 전, 데이터 검증 성공 ")
    void checkData_success() {
        // given
        Long userId = user.getId();
        long amount = order.getTotalPrice();

        // when & then
        assertDoesNotThrow(() -> paymentService.checkData(userId, order, amount));
    }


    @Test
    @DisplayName("결제 승인 전, 데이터 검증 실패 - 주문자와 유저 정보가 다름")
    void checkData_userMismatch_fail() {
        // given
        Long userId = 2L;
        long amount = order.getTotalPrice();

        // when & then
        assertNotEquals(Optional.of(order.getUser().getId()), userId);

        assertThatThrownBy(() -> paymentService.checkData(userId, order, amount))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.USER_MISMATCH);
    }

    @Test
    @DisplayName("결제 승인 전, 데이터 검증 실패 - 주문서의 금액과 결제할 금액이 다름")
    void checkData_amountMismatch_fail() {
        // given
        Long userId = user.getId();
        long amount = 100000L;

        // when & then
        assertNotEquals(Optional.of(order.getTotalPrice()), amount);

        assertThatThrownBy(() -> paymentService.checkData(userId, order, amount))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);

    }

    @Test
    @DisplayName("결제 승인 전, Payment 엔티티 생성 성공")
    // org.json.simple의 JSONObject가 제너릭 타입 없이 구현되어 있어서 발생하는 경고무시
    @SuppressWarnings("unchecked")
    void createPayment_success() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", order.getId());
        response.put("paymentKey", "testPaymentKey");
        response.put("amount", order.getTotalPrice());

        given(orderService.getOrder(order.getId()))
            .willReturn(order);

        // when
        paymentService.createPayment(response);

        // then
        // PaymentRepository.save() 메서드 호출 확인
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals("testPaymentKey", savedPayment.getPaymentKey());
        assertEquals(order.getId(), savedPayment.getOrder().getId());
        assertEquals(10000L, (long) savedPayment.getAmount());

    }

    @Test
    @DisplayName("결제 승인 전, Payment 엔티티 생성 실패 - 주문 정보 없음")
    // org.json.simple의 JSONObject가 제너릭 타입 없이 구현되어 있어서 발생하는 경고무시
    @SuppressWarnings("unchecked")
    void createPayment_notExistsOrder_fail() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", order.getId());
        response.put("paymentKey", "testPaymentKey");
        response.put("amount", order.getTotalPrice());

        given(orderService.getOrder((String) response.get("orderId")))
            .willThrow(new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER));

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(response))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(OrdersErrorCode.NOT_EXISTS_ORDER);
    }

    @Test
    @DisplayName("결제 승인 거절 시, isAborted 값 true로 업데이트 성공")
    void updateAborted_success() {
        // given
        String paymentKey = "testPaymentKey";
        Payment payment = spy(Payment.toEntity(paymentKey, order, order.getTotalPrice()));

        given(paymentRepository.findById(paymentKey))
            .willReturn(Optional.of(payment));

        // when
        paymentService.updateAborted(paymentKey);

        // then
        then(payment).should(times(1)).updateAborted();
        assertTrue(payment.isAborted());
    }

    @Test
    @DisplayName("결제 승인 거절 시, isAborted 값 true로 업데이트 실패 - 결제 정보 없음")
    void updateAborted_notExistsPayment_fail() {
        // given
        String paymentKey = "testPaymentKey";

        given(paymentRepository.findById(paymentKey))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.updateAborted(paymentKey))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.NOT_EXISTS_PAYMENT);
    }

    @Test
    @DisplayName("결제 승인 완료 시, approvedAt 및 method 값 저장 성공")
    // org.json.simple의 JSONObject가 제너릭 타입 없이 구현되어 있어서 발생하는 경고무시
    @SuppressWarnings("unchecked")
    void approvedPayment_success() {
        // given
        JSONObject response = new JSONObject();
        response.put("paymentKey", "testPaymentKey");
        response.put("approvedAt", "2025-02-24T21:11:59.000000+09:00");
        response.put("method", "간편결제");

        String paymentKey = response.get("paymentKey").toString();
        String date = response.get("approvedAt").toString();
        String method = response.get("method").toString();

        Payment payment = spy(Payment.toEntity(paymentKey, order, order.getTotalPrice()));

        given(paymentRepository.findById(paymentKey))
            .willReturn(Optional.of(payment));

        OffsetDateTime odt = OffsetDateTime.parse(date);

        // when
        paymentService.approvedPayment(response);

        // then
        then(payment).should(times(1)).approvedPayment(date, method);
        assertEquals(payment.getMethod(), method);
        assertEquals(payment.getApprovedAt(), odt.toLocalDateTime());
    }

    @Test
    @DisplayName("결제 승인 완료 시, approvedAt 및 method 값 저장 실패 - 결제 정보 없음")
    // org.json.simple의 JSONObject가 제너릭 타입 없이 구현되어 있어서 발생하는 경고무시
    @SuppressWarnings("unchecked")
    void approvedPayment_notExistsPayment_fail() {
        // given
        JSONObject response = new JSONObject();
        response.put("paymentKey", "testPaymentKey");
        response.put("approvedAt", "2025-02-24T21:11:59.000000+09:00");
        response.put("method", "간편결제");

        String paymentKey = response.get("paymentKey").toString();

        given(paymentRepository.findById(paymentKey))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.approvedPayment(response))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.NOT_EXISTS_PAYMENT);
    }

    @Test
    @DisplayName("결제 취소 시, Payment isCancelled 값 업데이트 및 order 주문상태 변경 실패 - 결제 정보 없음")
    // org.json.simple의 JSONObject가 제너릭 타입 없이 구현되어 있어서 발생하는 경고무시
    @SuppressWarnings("unchecked")
    void paymentCancelled_notExistsPayment_fail() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", "testOrderId");
        response.put("paymentKey", "testPaymentKey");

        String paymentKey = response.get("paymentKey").toString();

        given(paymentRepository.findById(paymentKey))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.paymentCancelled("testOrderId", paymentKey, "", order.getTotalPrice()))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(PaymentErrorCode.NOT_EXISTS_PAYMENT);
    }

}
