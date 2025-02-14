package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.popularItem.dto.PopularItemDto;
import com.example.Sparta_Store.popularItem.repository.LikedItemRepository;
import com.example.Sparta_Store.popularItem.repository.SoldItemRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SoldItemRepository soldItemRepository;
    private final LikedItemRepository likedItemRepository;
    private final ItemRepository itemRepository;

    // 판매량 기준 인기 상품 조회 (최근 N일) 디폴트 30일
    public List<PopularItemDto> getMostPopularSoldItems(int days) {

        LocalDate startDate = LocalDate.now().minusDays(days); // 현재날짜에서 30일(디폴트값)을 빼기 2025/02/11 - 30일 이런식으로 근 한달간 판매된 상품 조회

        // Object리스트엔 [상품id,총판매량] 이 들어있고 jpql 기반으로 인기 상품을 가져옴

        List<Object[]> results = soldItemRepository.findMostPopularSoldItems(startDate);

        return ToItemDto(results);
    }

    // 좋아요 기준 인기 상품 조회 (최근 N일)
    public List<PopularItemDto> getMostPopularLikedItems() {
        List<Object[]> results = likedItemRepository.findMostLikedItems();
        return ToItemDto(results);
    }

    // 결과를 dto로 반환하는 메서드
    private List<PopularItemDto> ToItemDto(List<Object[]> results) {
        List<Long> itemIds = results.stream()
                .map(row -> (Long) row[0]) // 상품 ID 추출
                .collect(Collectors.toList());

        // ID에 해당하는 상품 조회
        List<Item> items = itemRepository.findAllById(itemIds);
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        return results.stream()
                .map(row -> {
                    Item item = itemMap.get((Long) row[0]);
                    return new PopularItemDto(
                            item.getId(),
                            item.getName(),
                            item.getImgUrl(),
                            item.getPrice(),
                            item.getDescription(),
                            item.getStockQuantity(),
                            ((Number) row[1]).intValue() // ✅ rankValue (좋아요 개수 or 판매량)
                    );
                })
                .collect(Collectors.toList());
    }



}
