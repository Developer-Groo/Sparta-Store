package com.example.Sparta_Store.payment;

import com.example.Sparta_Store.cart.service.CartRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j(topic = "PaymentEventListener")
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final CartRedisService cartRedisService;

    @Async
    @EventListener
    public void resetCart(PaymentApprovedEvent event) {
        Long userId = event.getUserId();

        try {
            cartRedisService.deleteCartItem(userId);
            log.info("장바구니 초기화 완료 (userId = {})", userId);
        } catch (Exception e) {
            log.warn("장바구니 초기화 중 예외 발생 (userId = {}): {}", userId, e.getMessage());
        }
    }

}
