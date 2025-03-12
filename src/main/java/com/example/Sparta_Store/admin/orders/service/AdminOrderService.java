package com.example.Sparta_Store.admin.orders.service;

import static com.example.Sparta_Store.orders.OrderStatus.statusUpdatable;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.event.OrderCancelledEvent;
import com.example.Sparta_Store.orders.exception.OrdersErrorCode;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import com.example.Sparta_Store.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AdminOrderService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrdersRepository ordersRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 상태 변경
     */
    @Transactional
    public void updateOrderStatus(String orderId, UpdateOrderStatusDto requestDto)
        throws Exception {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER)
        );

        OrderStatus originStatus = order.getOrderStatus();
        OrderStatus requestStatus = OrderStatus.of(requestDto.orderStatus());

        isStatusUpdatable(originStatus, requestStatus); // 주문상태 변경 가능 여부
        order.updateOrderStatus(requestStatus);

        // 주문취소로 변경 시 결제 승인 취소
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_PAYMENT)
        );

        if (requestStatus.equals(OrderStatus.CANCELLED)) {
            paymentService.paymentCancelled(
                orderId, payment.getPaymentKey(), "", order.getTotalPrice()
            );
            // 상품 재고 복구 비동기 이벤트 발행
            eventPublisher.publishEvent(OrderCancelledEvent.toEvent(order));
        }

        log.info("주문상태 변경 완료 >> {}: {}", orderId, requestDto.orderStatus());
    }

    // 주문상태 변경 가능 여부
    public void isStatusUpdatable(OrderStatus originStatus, OrderStatus requestStatus) {
        if(requestStatus == OrderStatus.CONFIRMED) {
            throw new CustomException(OrdersErrorCode.ORDER_COMPLETION_NOT_ALLOWED);
        }

        if (!statusUpdatable.get(requestStatus).equals(originStatus)) {
            log.info("{} 상태에서는 {} 상태로 변경할 수 없습니다. ", originStatus, requestStatus);
            throw new CustomException(OrdersErrorCode.ORDER_STATUS_INVALID_TRANSITION);
        }
    }

}
