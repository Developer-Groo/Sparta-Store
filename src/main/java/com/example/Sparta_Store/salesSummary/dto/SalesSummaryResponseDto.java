package com.example.Sparta_Store.salesSummary.dto;

import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import java.time.LocalDateTime;

public record SalesSummaryResponseDto(Long itemId, LocalDateTime createdAt, int totalSales, String itemName) {

    public static SalesSummaryResponseDto toEntityAddName(SalesSummary salesSummary) {
        return new SalesSummaryResponseDto(
                salesSummary.getItem().getId(),
                salesSummary.getCreatedAt(),
                salesSummary.getTotalSales(),
                salesSummary.getItem().getName()
        );
    }
}
