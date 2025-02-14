package com.example.Sparta_Store.popularItem.dto;

public record PopularItemDto(
        Long id,
        String name,
        String imgUrl,
        int price,
        String description,
        int stockQuantity,
        int rankValue // 원래는 판매량 dto , 좋아요 dto 따로 만들어야 하지만 이렇게 하면 1개만 만들어도 댐
) {

}
