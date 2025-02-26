package com.example.Sparta_Store.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentApprovedEvent {
    private final Long userId;
}
