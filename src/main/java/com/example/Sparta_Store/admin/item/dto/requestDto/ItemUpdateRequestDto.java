package com.example.Sparta_Store.admin.item.dto.requestDto;

public record ItemUpdateRequestDto(
        String name,
        String imgUrl,
        Integer price,
        String description,
        Integer stockQuantity
) {
}