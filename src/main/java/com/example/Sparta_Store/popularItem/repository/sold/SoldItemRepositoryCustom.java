package com.example.Sparta_Store.popularItem.repository.sold;

import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import java.time.LocalDate;
import java.util.List;

public interface SoldItemRepositoryCustom {
    List<PopularItemRankValueDto> findMostPopularSoldItems(LocalDate startDate);
}
