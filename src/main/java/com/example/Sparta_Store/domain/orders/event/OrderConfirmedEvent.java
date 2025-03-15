package com.example.Sparta_Store.domain.orders.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderConfirmedEvent {
    private final Long userId;
    private final long totalPrice;
}
