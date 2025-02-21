package com.example.Sparta_Store.salesSummary.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.salesSummary.repository.SalesSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class SalesSummaryServiceTest {

    @InjectMocks
    private SalesSummaryService salesSummaryService;

    @Mock
    private SalesSummaryRepository salesSummaryRepository;

    private Item item;
    private LocalDateTime currentWeekStartDate;

    @BeforeEach
    void setUp() {
        item = new Item(1L, "test Item", "www.example.com", 10000, "test Item description", 100, null, null);

        currentWeekStartDate = LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
    }

//    @Test
    @DisplayName("기존에 주간 판매 데이터가 있을 경우 판매량이 증가해야 한다.")
    void shouldIncrementSales_If_SummaryExists() {
        // given
        SalesSummary summary = new SalesSummary(1L, null, 10);
        item.updateSalesSummary(summary);

        given(salesSummaryRepository.findByItemIdAndCreatedAt(item.getId(), currentWeekStartDate))
                .willReturn(Optional.of(item.getSalesSummary()));

        // when
        salesSummaryService.updateSales(item, 5);

        // then
        assertThat(item.getTotalSales()).isEqualTo(15);
        verify(salesSummaryRepository, never()).save(any());
        verifyNoMoreInteractions(salesSummaryRepository);
    }

//    @Test
    @DisplayName("해당 주의 첫 판매 시 새로운 SalesSummary 가 생성되어야 한다.")
    void shouldCreateNewSalesSummary_If_NoExistingSummary() {
        // given
        given(salesSummaryRepository.findByItemIdAndCreatedAt(item.getId(), currentWeekStartDate))
                .willReturn(Optional.empty());

        ArgumentCaptor<SalesSummary> captor = ArgumentCaptor.forClass(SalesSummary.class);
        // when
        salesSummaryService.updateSales(item, 5);

        // then
        then(salesSummaryRepository).should().save(captor.capture());
        SalesSummary summary = captor.getValue();

        assertThat(summary.getTotalSales()).isEqualTo(5);
        assertThat(summary.getItem()).isEqualTo(item);
    }

}