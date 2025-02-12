package com.example.Sparta_Store.OrderItem.dto.response;

import com.example.Sparta_Store.OrderItem.entity.OrderItem;
import java.time.LocalDateTime;

public record OrderItemResponseDto(
    Long itemId,
    String itemName,
    int quantity,
    int orderPrice,
    LocalDateTime createdAt
) {
    public static OrderItemResponseDto toDto(OrderItem orderItem) {
        return new OrderItemResponseDto(
            orderItem.getItem().getId(),
            orderItem.getItem().getName(),
            orderItem.getQuantity(),
            orderItem.getOrderPrice(),
            orderItem.getCreatedAt()
        );
    }

}
