package com.example.Sparta_Store.domain.salesSummary.service;

import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.domain.salesSummary.repository.SalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SalesSummaryService {

    private final SalesSummaryRepository repository;

    @Transactional
    public void updateSales(Item item, int quantity) {
        LocalDateTime currentWeekStartDate = LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);

        SalesSummary summary = repository.findByItemIdAndCreatedAt(item.getId(), currentWeekStartDate)
                .map(existingSummary -> {
                    existingSummary.incrementSales(quantity);
                    return existingSummary;
                })
                .orElseGet(() -> {
                    SalesSummary salesSummary = SalesSummary.toEntity(quantity);
                    item.updateSalesSummary(salesSummary);
                    repository.save(salesSummary);
                    return salesSummary;
                });
    }
}
