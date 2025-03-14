package com.example.Sparta_Store.domain.popularItem.controller;

import com.example.Sparta_Store.domain.likes.dto.response.LikesDto;
import com.example.Sparta_Store.domain.popularItem.service.PopularItemService;
import com.example.Sparta_Store.domain.salesSummary.dto.SalesSummaryResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/popularItems")
@RequiredArgsConstructor
public class PopularItemController {

    private final PopularItemService popularItemService;

    // 판매량 기준 인기 상품 조회
    @GetMapping("/sold")
    public ResponseEntity<List<SalesSummaryResponseDto>> getPopularItemBySales() {
        List<SalesSummaryResponseDto> response = popularItemService.getMostPopularSoldItems();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 찜 기준 인기 상품 조회
    @GetMapping("/liked")
    public ResponseEntity<List<LikesDto>> getPopularProductsByLikes() {
        List<LikesDto> response = popularItemService.getMostPopularLikedItems();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 카테고리별 검색순위
    @GetMapping("/{category}")
    public List<String> getScore(@PathVariable String category){
        return popularItemService.getCategoryRanking(category);
    }
}
