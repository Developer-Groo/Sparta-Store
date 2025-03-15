package com.example.Sparta_Store.domain.item.dto.response;

import com.example.Sparta_Store.domain.item.entity.Item;
import java.io.Serializable;

public record ItemResponseDto(
        Long id,
        String name,
        String url,
        int price,
        String description,
        int totalSales
) implements Serializable { // 레디스를 사용하기 위한 직렬화 추가

    public static ItemResponseDto toDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getImgUrl(),
                item.getPrice(),
                item.getDescription(),
                item.getTotalSales()
        );
    }
}
