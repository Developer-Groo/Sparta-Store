package com.example.Sparta_Store.domain.email.listener;

import com.example.Sparta_Store.domain.email.event.OrderStatusUpdatedEvent;
import com.example.Sparta_Store.domain.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusUpdatedListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        String status = event.orderStatus().getDescription();

        try {
            emailService.sendEmail(
                event.userEmail(),
                "[스파르타스토어] "+ status + " 안내 메일",
                "<" + status + ">\n" +
                    event.userName() + "님의 주문(" + event.orderId() + ")이 정상적으로 " + status + " 되었습니다."
            );
            log.info("메일 전송 완료");
        } catch (Exception e) {
            log.error("메일 전송 중 예외 발생 (orderId = {}) : {}", event.orderId(), e.getMessage());
        }
    }

}
