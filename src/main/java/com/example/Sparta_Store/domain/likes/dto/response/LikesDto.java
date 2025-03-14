package com.example.Sparta_Store.domain.likes.dto.response;

import com.example.Sparta_Store.domain.item.entity.Item;

public record LikesDto(Long itemId, String itemName, Long totalLikes) {

    public static LikesDto convertFromItem(Item item, Long totalLikes) {
        return new LikesDto(
                item.getId(),
                item.getName(),
                totalLikes
        );
}

}
