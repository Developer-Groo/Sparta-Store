package com.example.Sparta_Store.salesSummary.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.salesSummary.repository.SalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesSummaryService {

    private final SalesSummaryRepository repository;

    @Transactional
    public void updateSales(Item item, int quantity) {
        SalesSummary summary = repository.findSalesSummaryByItemId(item.getId())
                .map(existingSummary -> {
                    existingSummary.incrementSales(quantity);
                    return existingSummary;
                })
                .orElseGet(() -> SalesSummary.toEntity(item, quantity));

        repository.save(summary);
    }
}
