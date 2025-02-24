package com.example.Sparta_Store.orders.dto.response;

import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import java.time.LocalDateTime;

public record OrderResponseDto(
    String orderId,
    long totalPrice,
    OrderStatus orderStatus,
    LocalDateTime createdAt
) {
    public static OrderResponseDto toDto(Orders order) {
        return new OrderResponseDto(
            order.getId(),
            order.getTotalPrice(),
            order.getOrderStatus(),
            order.getCreatedAt()
        );
    }

}
