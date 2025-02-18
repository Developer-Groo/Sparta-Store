package com.example.Sparta_Store.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record ReviewRequestDto(
        @NotBlank
        String content,
        String imgUrl,
        @Range(min = 1, max = 5)
        int rating
) {
}
