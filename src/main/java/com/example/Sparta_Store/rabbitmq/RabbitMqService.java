package com.example.Sparta_Store.rabbitmq;

import com.example.Sparta_Store.coupon.service.CouponService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j(topic = "RabbitMqService")
@RequiredArgsConstructor
@Service
public class RabbitMqService {

    private final CouponService couponService;

    // Queue에서 메시지를 구독 (Consumer)
    @RabbitListener(queues = "${rabbitmq.queue.name}", concurrency = "4")
    public void receiveCouponIssuanceMessage(Map<String, Object> message) {
        try {
            Long userId = Long.valueOf(message.get("userId").toString());
            String couponName = message.get("couponName").toString();
            Long selectedCoupon = Long.parseLong(message.get("selectedCoupon").toString());

            couponService.saveCouponUser(userId, couponName, selectedCoupon);
        } catch (Exception e) {
            log.error("쿠폰 발급 이력 DB 저장 실패: {}", message, e);
            throw new AmqpRejectAndDontRequeueException("쿠폰 발급 이력 저장 실패", e);
        }
    }
}
