package com.example.Sparta_Store.domain.payment.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentApprovedEvent {
    private final Long userId;
}
