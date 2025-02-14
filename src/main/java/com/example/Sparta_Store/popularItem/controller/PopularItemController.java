package com.example.Sparta_Store.popularItem.controller;

import com.example.Sparta_Store.popularItem.dto.PopularItemDto;
import com.example.Sparta_Store.popularItem.service.PopularItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popularItems")
@RequiredArgsConstructor
public class PopularItemController {

    private final PopularItemService popularItemService;

    // 판매량 기준 인기 상품 조회 (최근 7일)
    @GetMapping("/sold")
    public List<PopularItemDto> getPopularItemBySales(
            @RequestParam(defaultValue = "7") int days) { // 클라이언트가 값을 입력 안하면 7일로
        return popularItemService.getMostPopularSoldItems(days);
    }

    @GetMapping("/liked")
    public List<PopularItemDto> getPopularProductsByLikes() {
        return popularItemService.getMostPopularLikedItems();
    }
}
