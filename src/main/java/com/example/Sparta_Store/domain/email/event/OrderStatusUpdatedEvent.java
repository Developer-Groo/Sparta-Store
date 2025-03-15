package com.example.Sparta_Store.domain.email.event;

import com.example.Sparta_Store.domain.orders.OrderStatus;
import com.example.Sparta_Store.domain.orders.entity.Orders;

public record OrderStatusUpdatedEvent(
    String orderId,
    String userEmail,
    String userName,
    OrderStatus orderStatus,
    long price
) {
    public static OrderStatusUpdatedEvent toEvent(Orders orders) {
        return new OrderStatusUpdatedEvent(
            orders.getId(),
            orders.getUser().getEmail(),
            orders.getUser().getName(),
            orders.getOrderStatus(),
            orders.getTotalPrice()
        );
    }
}
