package com.example.Sparta_Store.domain.category.dto.response;

import com.example.Sparta_Store.domain.category.entity.Category;

import java.util.List;

public record CategoryResponseDto(
        Long id,
        String name,
        List<CategoryResponseDto> children
) {

    public static CategoryResponseDto toDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getChildren().stream()
                        .map(CategoryResponseDto::toDto)
                        .toList()
        );
    }
}
