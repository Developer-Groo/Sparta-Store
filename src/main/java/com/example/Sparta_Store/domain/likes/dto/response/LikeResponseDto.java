package com.example.Sparta_Store.domain.likes.dto.response;

import com.example.Sparta_Store.domain.likes.entity.Likes;

public record LikeResponseDto(
        Long likeId,
        Long itemId,
        String itemName
) {

    public static LikeResponseDto toDto(Likes likes) {
        return new LikeResponseDto(
                likes.getId(),
                likes.getItem().getId(),
                likes.getItem().getName()
        );
    }

}
