package com.example.Sparta_Store.salesSummary.dto;

import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import java.time.LocalDateTime;

public record SalesSummaryDto(Long itemId, LocalDateTime createdAt, int totalSales) {

    public static SalesSummaryDto toEntity(SalesSummary salesSummary) {
        return new SalesSummaryDto(
                salesSummary.getItem().getId(),
                salesSummary.getCreatedAt(),
                salesSummary.getTotalSales()
        );
    }
}
