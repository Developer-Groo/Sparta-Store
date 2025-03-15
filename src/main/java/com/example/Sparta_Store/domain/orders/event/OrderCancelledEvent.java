package com.example.Sparta_Store.domain.orders.event;

import com.example.Sparta_Store.domain.orders.entity.Orders;

public record OrderCancelledEvent(Orders order) {
    public static OrderCancelledEvent toEvent(Orders order) {
        return new OrderCancelledEvent(order);
    }
}
