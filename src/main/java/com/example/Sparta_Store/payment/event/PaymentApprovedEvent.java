package com.example.Sparta_Store.payment.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentApprovedEvent {
    private final Long userId;
}
