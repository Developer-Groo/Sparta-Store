package com.example.Sparta_Store.salesSummary.dto;

import java.time.LocalDateTime;

public record SalesSummaryDto(Long itemId, LocalDateTime createdAt, int totalSales) {
}
