package com.example.Sparta_Store.domain.popularItem.service;

import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.likes.dto.response.LikesDto;
import com.example.Sparta_Store.domain.likes.repository.LikesRepository;
import com.example.Sparta_Store.domain.salesSummary.dto.SalesSummaryResponseDto;
import com.example.Sparta_Store.domain.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.domain.salesSummary.repository.SalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SalesSummaryRepository salesSummaryRepository;
    private final LikesRepository likesRepository;
    private final ItemRepository itemRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<SalesSummaryResponseDto> getMostPopularSoldItems() {

        LocalDateTime week = LocalDateTime.now().minusDays(7);

        List<SalesSummary> mostPopularSoldItems = salesSummaryRepository.findByCreatedAtAfterOrderByTotalSalesDesc(week);

        return mostPopularSoldItems.stream()
                .map(SalesSummaryResponseDto::toEntityAddName)
                .toList();
    }

    public Long getLikeCount(Long itemId) {
        return likesRepository.countByItemId(itemId);
    }

    public List<LikesDto> getMostPopularLikedItems() {

        List<Long> likedItemIds = likesRepository.findDistinctItemIds();

        return likedItemIds.stream()
                .map(itemId -> {
                    Item item = itemRepository.findById(itemId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

                    return LikesDto.convertFromItem(item, likesRepository.countByItemId(itemId));
                })
                .sorted(Comparator.comparing(LikesDto::totalLikes).reversed())
                .toList();
    }

    public List<String> getCategoryRanking(String category) {

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        Set<String> topItems = zSetOps.reverseRange(category, 0, 4);

        List<String> rankedItems = new ArrayList<>();
        int rank = 1;

        for (String item : topItems) {
            rankedItems.add(rank + "." + item);
            rank++;
        }

        return rankedItems;
    }
}




