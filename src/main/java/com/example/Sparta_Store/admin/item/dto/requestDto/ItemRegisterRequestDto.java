package com.example.Sparta_Store.admin.item.dto.requestDto;

import com.example.Sparta_Store.category.entity.Category;
import jakarta.validation.constraints.NotBlank;

public record ItemRegisterRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String imgUrl,
        @NotBlank
        int price,
        @NotBlank
        String description,
        @NotBlank
        int stockQuantity,
        Category category
) {
}
