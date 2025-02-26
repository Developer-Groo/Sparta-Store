package com.example.Sparta_Store.email.listener;

import com.example.Sparta_Store.email.event.ItemRestockedEvent;
import com.example.Sparta_Store.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemRestockedListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleItemRestocked(ItemRestockedEvent event) {
        for (String email : event.userEmails()) {
            emailService.sendEmail(
                    email,
                    "재입고 알림",
                    "안녕하세요, 고객님이 찜한 상품: " + "[" + event.itemId() + "] : "
                            + event.name() + " 상품이 재입고 되었습니다."
            );
        }
    }
}
