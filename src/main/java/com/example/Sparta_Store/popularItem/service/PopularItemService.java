package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.entity.QItem;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.likes.entity.QLikes;
import com.example.Sparta_Store.popularItem.dto.PopularItemDto;
import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import com.example.Sparta_Store.popularItem.repository.liked.LikedItemRepository;
import com.example.Sparta_Store.popularItem.repository.sold.SoldItemRepository;
import com.example.Sparta_Store.salesSummary.dto.SalesSummaryDto;
import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.salesSummary.repository.SalesSummaryRepository;
import com.querydsl.core.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SoldItemRepository soldItemRepository;
    private final LikedItemRepository likedItemRepository; // 레포지토리를 3가지로 나눈 이유는 단일책임원칙때문이다 , 기본적인 crud를 사용하지 않을꺼면 그냥 레포지토리는 삭제해도 될 듯?
    private final ItemRepository itemRepository;
    private final SalesSummaryRepository salesSummaryRepository;

    // 판매량 기준 인기 상품 조회 (최근 N일) 디폴트 7일
    public List<SalesSummaryDto> getMostPopularSoldItems(Long itemId, LocalDateTime createdAt, int totalSales) {

        List<PopularItemRankValueDto> mostPopularSoldItems = soldItemRepository.findMostPopularSoldItems(); // 최근 N일 동안 가장 많이 판매된 상품 목록을 조회.

        Optional<SalesSummary> byItemIdAndCreatedAtAndTotalSales = salesSummaryRepository.findByItemIdAndCreatedAtAndTotalSales(
                itemId, createdAt, totalSales);

        return toDto(byItemIdAndCreatedAtAndTotalSales); // 조회된 데이터를 toItemDto를 통해 PopularItemDto로 변환하여 반환.
    }

//    // 좋아요 기준 인기 상품 조회
//    public List<PopularItemDto> getMostPopularLikedItems() {
//
//        List<PopularItemRankValueDto> mostPopularLikedItems = likedItemRepository.findMostLikedItems(); // 레포지토리에서 쿼리를 실행하고 List<PopularItemRankValueDto> [itemId, 좋아요 개수]데이터 가 들어있음
//
//        return toItemDto(mostPopularLikedItems); // 조회된 데이터를 toItemDto를 통해 PopularItemDto로 변환하여 반환.
//    }

//    // 결과를 dto로 반환하는 메서드
//    private List<PopularItemDto> toItemDto(List<PopularItemRankValueDto> soldOrLikedItems) { // List<PopularItemRankValueDto>를 받아 List<PopularItemDto>로 변환하는 메소드
//
//        List<Long> itemIds = soldOrLikedItems.stream()
//                .map(PopularItemRankValueDto::itemId) // soldOrLikedItems에서 상품id만 추출 > List<Long>으로 변환
//                .collect(Collectors.toList()); // 상품 id 목록
//
//        // ID에 해당하는 상품 조회
//        List<Item> items = itemRepository.findAllById(itemIds); // 위에서 id추출했으니까 조회
//
//        Map<Long, Item> itemMap = items.stream()
//                .collect(Collectors.toMap(Item::getId, item -> item)); // items 리스트를 Map<상품ID, Item> 형태로 변환 >> 이유는 상품정보조회가 빠르기 때문
//
//        return soldOrLikedItems.stream()
//                .map(soldOrLikedItem -> { // soldOrViewedItems 리스트를 스트림으로 순회하면서 PopularItemDto로 변환
//
//                    Item item = itemMap.get(soldOrLikedItem.itemId());  // 위에서 Map<상품ID, Item> 형태로 변환 했고 여기서 itemid에 대한 상품을 찾음
//
//                    return new PopularItemDto(
//                            item.getId(),
//                            item.getName(),
//                            item.getImgUrl(),
//                            item.getPrice(),
//                            item.getDescription(),
//                            item.getStockQuantity(),
//                            soldOrLikedItem.rankValue() // rankValue (좋아요 개수 or 판매량)
//                    ); //PopularItemDto 객체를 생성하여 List<PopularItemDto>에 추가 rankValue는 좋아요 or 판매량
//                })
//                .collect(Collectors.toList()); // 변환된 PopularItemDto 리스트를 반환
//
//    }
//
//    // DTO 변환하는 메서드(판매량순)
//    public static List<PopularItemRankValueDto> soldItemToDto(List<Tuple> idAndSoldNums) {
//        return idAndSoldNums.stream()
//                .map(tuple -> new PopularItemRankValueDto(
//                        tuple.get(QItem.item.id),
//                        tuple.get(QItem.item.salesSummary.totalSales) != null ?
//                                tuple.get(QItem.item.salesSummary.totalSales).intValue() : 0 // null체크 > 만약 null이면 0으로 반환
//                )) // IdAndSoldNum 객체 에서 item.id와 좋아요 갯수를 추출하고 PopularItemRankValueDto 타입의 dto로 변환
//                .collect(Collectors.toList()); // List<PopularItemRankValueDto> 로 변환
//    }

    // DTO 변환하는 메서드(찜순)
    public static List<PopularItemRankValueDto> likedItemToDto(List<Tuple> idAndLikedNums) {
        return idAndLikedNums.stream()
                .map(tuple -> new PopularItemRankValueDto(
                        tuple.get(QLikes.likes.item.id),
                        tuple.get(QLikes.likes.count()).intValue() // IdAndLikedNum 객체 에서 item.id와 좋아요 갯수를 추출하고 PopularItemRankValueDto 타입의 dto로 변환
                ))
                .collect(Collectors.toList()); // 위에 변환한 dto를 List<PopularItemRankValueDto> 로 변환
    }

    public static List<SalesSummaryDto> toDto(Optional<SalesSummary> findThings) {
        return findThings.stream()
                .map(salesSummary -> new SalesSummaryDto(
                        findThings.get().getId(),
                        findThings.get().getCreatedAt(),
                        findThings.get().getTotalSales()
                ))
                .collect(Collectors.toList());
    }
}
