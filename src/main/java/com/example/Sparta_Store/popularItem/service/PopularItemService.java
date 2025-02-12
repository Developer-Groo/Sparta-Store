package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.popularItem.repository.SoldItemRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SoldItemRepository soldItemRepository;
    private final ItemRepository itemRepository;

    // 판매량 기준 인기 상품 조회 (최근 N일) 디폴트 30일
    public List<Item> getMostPopularItems(int days) {

        LocalDate startDate = LocalDate.now().minusDays(days); // 현재날짜에서 30일(디폴트값)을 빼기 2025/02/11 - 30일 이런식으로 근 한달간 판매된 상품 조회

        List<Item> results = soldItemRepository.findMostPopularItems(startDate); // Object리스트엔 [상품id,총판매량] 이 들어있고 jpql 기반으로 인기 상품을 가져옴

        return results;
    }

//
//    // 결과를 item 엔티티로 변환하는 공통 메서드
//    private List<Item> mapToItems(List<Object[]> results) {
//        List<Long> itemIds = results.stream() // 위에서 받은 쿼리 결과 리스트를 스트림으로 변환 results = [상품 id, 총 판매량]형식의 리스트
//                .map(row -> (Long) row[0]) // results에 있는 배열 중에서 첫번째(상품id)만 가져옴 object 타입이므로 Long으로 변환
//                .collect(Collectors.toList()); // 상품id만 빼낸 걸 list로 변환하여 저장
//        return itemRepository.findAllById(itemIds); // 많이 팔린 상품들의 id를  가져와서 조회 정보들을 가져와서 반환
//    }

}
