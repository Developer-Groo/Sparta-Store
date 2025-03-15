package com.example.Sparta_Store.admin.item.dto.requestDto;

import com.example.Sparta_Store.domain.category.entity.Category;

public record ItemUpdateRequestDto(
        String name,
        String imgUrl,
        Integer price,
        String description,
        Integer stockQuantity,
        Category category
) {
}