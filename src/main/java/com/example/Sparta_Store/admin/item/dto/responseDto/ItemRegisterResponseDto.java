package com.example.Sparta_Store.admin.item.dto.responseDto;

import com.example.Sparta_Store.domain.item.entity.Item;

public record ItemRegisterResponseDto(
        String name,
        String imageUrl,
        int price,
        String description,
        int stockQuantity,
        Long categoryId
) {
    public static ItemRegisterResponseDto toDto(Item item) {
        return new ItemRegisterResponseDto(
                item.getName(),
                item.getImgUrl(),
                item.getPrice(),
                item.getDescription(),
                item.getStockQuantity(),
                item.getCategory().getId()
        );
    }
}
