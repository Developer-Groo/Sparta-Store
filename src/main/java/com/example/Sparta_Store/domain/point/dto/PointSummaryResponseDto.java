package com.example.Sparta_Store.domain.point.dto;

import com.example.Sparta_Store.domain.point.SummaryType;
import com.example.Sparta_Store.domain.point.entity.PointSummary;
import java.time.LocalDateTime;

public record PointSummaryResponseDto(
        Long userId,
        Long amount,
        SummaryType SummaryType,
        LocalDateTime createdAt
) {
    public static PointSummaryResponseDto toEntity(PointSummary summary) {
        return new PointSummaryResponseDto(
                summary.getUser().getId(),  // 사용자 ID만 포함
                summary.getAmount(),
                summary.getSummaryType(),
                summary.getCreatedAt()
        );
    }
}
