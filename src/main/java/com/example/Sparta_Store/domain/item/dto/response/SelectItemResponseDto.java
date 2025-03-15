package com.example.Sparta_Store.domain.item.dto.response;

import com.example.Sparta_Store.domain.item.entity.Item;

public record SelectItemResponseDto (
  String name,
  String img_url,
  String description,
  int price,
  int stock_quantity
){
    public static SelectItemResponseDto from(Item item) {
        return new SelectItemResponseDto(
                item.getName(),
                item.getImgUrl(),
                item.getDescription(),
                item.getPrice(),
                item.getStockQuantity()
        );
    }
}
