package com.example.Sparta_Store.cache.service;

import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCacheService {

    private final ItemService itemService; // 기존 서비스 호출

    @Cacheable(value = "itemSearch", key = "#inStock + '_' + #keyword + '_' + #pageQuery.page + '_' + #pageQuery.size", unless = "#result.isEmpty()")
    public PageResult<ItemResponseDto> getSearchItems(boolean inStock, String keyword, PageQuery pageQuery) {
        log.info("DB 조회 실행 - 검색어: {}, 재고여부: {}, 페이지: {}, 사이즈: {}", keyword, inStock, pageQuery.getPage(), pageQuery.getSize());
        return itemService.getSearchItems(inStock, keyword, pageQuery);
    }

    @CacheEvict(value = "itemSearch", allEntries = true)
    public void clearItemCache() {
        log.info("상품 검색 캐시 삭제");
    }
}
