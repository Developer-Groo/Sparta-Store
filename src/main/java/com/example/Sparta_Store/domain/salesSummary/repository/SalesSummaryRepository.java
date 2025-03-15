package com.example.Sparta_Store.domain.salesSummary.repository;

import com.example.Sparta_Store.domain.salesSummary.entity.SalesSummary;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {

    Optional<SalesSummary> findByItemIdAndCreatedAt(Long itemId, LocalDateTime createdAt);

    List<SalesSummary> findByCreatedAtAfterOrderByTotalSalesDesc(LocalDateTime startDate);
}
