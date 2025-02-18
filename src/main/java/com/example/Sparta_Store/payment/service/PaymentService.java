package com.example.Sparta_Store.payment.service;

import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    @Transactional
    public void CreatePayment(JSONObject response, Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));

        OffsetDateTime odt = OffsetDateTime.parse(response.get("approvedAt").toString());
        LocalDateTime approvedAt = odt.toLocalDateTime();

        Payment savedPayment = new Payment(
            response.get("paymentKey").toString(),
            user,
            response.get("orderId").toString(),
            approvedAt,
            response.get("method").toString()
        );
        paymentRepository.save(savedPayment);

        // 주문 생성 호출
        orderService.checkoutOrder(savedPayment, user.getId());
    }

}
