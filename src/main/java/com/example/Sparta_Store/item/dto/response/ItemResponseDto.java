package com.example.Sparta_Store.item.dto.response;

import com.example.Sparta_Store.item.entity.Item;

public record ItemResponseDto(
        Long id,
        String name,
        String url,
        int price,
        String description,
        int totalSales
) {

    public static ItemResponseDto toDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getImgUrl(),
                item.getPrice(),
                item.getDescription(),
                item.getTotalSales()
        );
    }
}
