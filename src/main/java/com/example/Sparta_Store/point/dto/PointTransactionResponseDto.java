package com.example.Sparta_Store.point.dto;

import com.example.Sparta_Store.point.TransactionType;
import com.example.Sparta_Store.point.entity.PointTransaction;
import java.time.LocalDateTime;

public record PointTransactionResponseDto(
        Long userId,
        Long amount,
        TransactionType transactionType,
        LocalDateTime createdAt
) {
    public static PointTransactionResponseDto toEntity(PointTransaction transaction) {
        return new PointTransactionResponseDto(
                transaction.getUser().getId(),  // 사용자 ID만 포함
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCreatedAt()
        );
    }
}
