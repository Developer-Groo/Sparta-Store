package com.example.Sparta_Store.salesSummary.repository;

import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {

    Optional<SalesSummary> findSalesSummaryByItemId(Long itemId);
}
