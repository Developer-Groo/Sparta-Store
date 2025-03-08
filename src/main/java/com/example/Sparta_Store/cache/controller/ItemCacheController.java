package com.example.Sparta_Store.cache.controller;

import com.example.Sparta_Store.cache.service.ItemCacheService;
import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache/items")
@RequiredArgsConstructor
public class ItemCacheController {

    private final ItemCacheService itemCacheService;

    // 🔹 상품 검색 API (캐싱 적용)
    @GetMapping("/search")
    public ResponseEntity<PageResult<ItemResponseDto>> getSearchItems(
            @RequestParam boolean inStock,
            @RequestParam String keyword,
            @ModelAttribute PageQuery pageQuery) {
        PageResult<ItemResponseDto> items = itemCacheService.getSearchItems(inStock, keyword, pageQuery);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    // 🔹 캐시 삭제 API (관리용)
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearItemCache() {
        itemCacheService.clearItemCache();
        return ResponseEntity.status(HttpStatus.OK).body("상품 검색 캐시 삭제 완료");
    }
}

