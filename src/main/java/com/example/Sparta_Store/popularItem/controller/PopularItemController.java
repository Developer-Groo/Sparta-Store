package com.example.Sparta_Store.popularItem.controller;

import com.example.Sparta_Store.popularItem.service.PopularItemService;
import com.example.Sparta_Store.salesSummary.dto.SalesSummaryDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popularItems")
@RequiredArgsConstructor
public class PopularItemController {

    private final PopularItemService popularItemService;

    // 판매량 기준 인기 상품 조회
    @GetMapping("/sold")
    public List<SalesSummaryDto> getPopularItemBySales(Long itemId, LocalDateTime createdAt, int totalSales) {
        return popularItemService.getMostPopularSoldItems(itemId, createdAt, totalSales);
    }

//    @GetMapping("/liked")
//    public List<PopularItemDto> getPopularProductsByLikes() {
//        return popularItemService.getMostPopularLikedItems();
//    }
}
