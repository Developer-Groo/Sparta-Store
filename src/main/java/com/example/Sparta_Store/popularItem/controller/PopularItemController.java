package com.example.Sparta_Store.popularItem.controller;

import com.example.Sparta_Store.likes.dto.response.LikesDto;
import com.example.Sparta_Store.popularItem.service.PopularItemService;
import com.example.Sparta_Store.salesSummary.dto.SalesSummaryResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
