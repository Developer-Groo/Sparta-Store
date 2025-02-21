package com.example.Sparta_Store.payment.service;

import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import com.example.Sparta_Store.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PaymentService")
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;

    public boolean checkStatus(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        return order.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT);
    }

    @Transactional
    public void createPayment(JSONObject response) {

        String orderId = response.get("orderId").toString();
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        OffsetDateTime odt = OffsetDateTime.parse(response.get("approvedAt").toString());
        LocalDateTime approvedAt = odt.toLocalDateTime();

        Payment savedPayment = new Payment(
            response.get("paymentKey").toString(),
            order,
            approvedAt,
            response.get("method").toString()
        );
        paymentRepository.save(savedPayment);
    }

    public void checkData(Long userId, String orderId, long amount) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        if(!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
        if(order.getTotalPrice() != amount) {
            throw new IllegalArgumentException("결제 금액이 변동되었습니다.");
        }
    }

    @Transactional
    public void checkout(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        // 상품 재고 감소
        List<OrderItem> orderItemList = orderItemRepository.findOrderItemsByOrders(order).orElseThrow(
            () -> new IllegalArgumentException("주문 상품 정보를 찾을 수 없습니다.")
        );
        itemService.decreaseStock(orderItemList);
        // 주문상태 변경
        order.updateOrderStatus(OrderStatus.CONFIRMED);
    }

    // 결제 과정에서 오류가 발생하여 결제 취소
    @Transactional
    public void paymentCancelled(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        // 주문상태 변경
        order.updateOrderStatus(OrderStatus.PAYMENT_CANCELLED);
    }

}
