package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.likes.dto.response.LikesDto;
import com.example.Sparta_Store.likes.repository.LikesRepository;
import com.example.Sparta_Store.salesSummary.dto.SalesSummaryResponseDto;
import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.salesSummary.repository.SalesSummaryRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SalesSummaryRepository salesSummaryRepository;
    private final LikesRepository likesRepository;
    private final ItemRepository itemRepository;

    // 판매량 기준 인기 상품 조회 (최근 N일) 디폴트 7일
    public List<SalesSummaryResponseDto> getMostPopularSoldItems() {

        LocalDateTime week = LocalDateTime.now().minusDays(7);

        List<SalesSummary> mostPopularSoldItems = salesSummaryRepository.findByCreatedAtAfterOrderByTotalSalesDesc(
                week);

        return mostPopularSoldItems.stream()
                .map(SalesSummaryResponseDto::toEntityAddName)
                .collect(Collectors.toList());
    }

    public Long getLikeCount(Long itemId) {
        return likesRepository.countByItemId(itemId);
    }

    // 좋아요 기준 인기 상품 조회 (누적)
    public List<LikesDto> getMostPopularLikedItems() {

        // 좋아요가 있는 상품 ID 목록 조회 (중복 제거됨)
        List<Long> likedItemIds = likesRepository.findDistinctItemIds();

        return likedItemIds.stream()
                .map(itemId -> {
                    Item item = itemRepository.findById(itemId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

                    return LikesDto.convertFromItem(
                            item,
                            likesRepository.countByItemId(itemId) // 현재 좋아요 수 직접 조회
                    );
                })
                .sorted(Comparator.comparing(LikesDto::totalLikes).reversed()) // 좋아요 수 기준 정렬
                .collect(Collectors.toList());
    }
}




