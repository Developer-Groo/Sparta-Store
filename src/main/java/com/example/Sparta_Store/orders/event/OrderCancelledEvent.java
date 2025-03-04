package com.example.Sparta_Store.orders.event;

import com.example.Sparta_Store.orders.entity.Orders;

public record OrderCancelledEvent(Orders order) {
    public static OrderCancelledEvent toEvent(Orders order) {
        return new OrderCancelledEvent(order);
    }
}
