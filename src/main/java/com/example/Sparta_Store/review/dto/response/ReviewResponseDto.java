package com.example.Sparta_Store.review.dto.response;

import com.example.Sparta_Store.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Long userId,
        String userName,
        String content,
        String imgUrl,
        LocalDateTime createdAt
) {

    public static ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getContent(),
                review.getImgUrl(),
                review.getCreatedAt()
        );
    }
}
