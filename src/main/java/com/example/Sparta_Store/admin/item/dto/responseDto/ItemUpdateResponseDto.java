package com.example.Sparta_Store.admin.item.dto.responseDto;

public record ItemUpdateResponseDto(
        String name,
        String imgUrl,
        Integer price,
        String description,
        Integer stockQuantity
) {
}
