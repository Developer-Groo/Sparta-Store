package com.example.Sparta_Store.email.event;

import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;

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
