package com.example.Sparta_Store.domain.review.dto.response;

import com.example.Sparta_Store.domain.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Long userId,
        String userName,
        String content,
        String imgUrl,
        int rating,
        LocalDateTime createdAt
) {

    public static ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getName(),
                review.getContent(),
                review.getImgUrl(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
