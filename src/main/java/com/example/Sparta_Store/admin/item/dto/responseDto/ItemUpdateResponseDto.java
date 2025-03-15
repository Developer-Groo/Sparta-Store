package com.example.Sparta_Store.admin.item.dto.responseDto;

import com.example.Sparta_Store.domain.item.entity.Item;

public record ItemUpdateResponseDto(
        String name,
        String imgUrl,
        Integer price,
        String description,
        Integer stockQuantity,
        Long categoryId
) {
    public static ItemUpdateResponseDto toDto(Item item) {
        return new ItemUpdateResponseDto(
                item.getName(),
                item.getImgUrl(),
                item.getPrice(),
                item.getDescription(),
                item.getStockQuantity(),
                item.getCategory().getId()
        );
    }
}
