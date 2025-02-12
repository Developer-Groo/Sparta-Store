package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.popularItem.repository.SoldItemRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularItemService {

    private final SoldItemRepository soldItemRepository;

    // 판매량 기준 인기 상품 조회 (최근 N일) 디폴트 30일
    public List<Item> getMostPopularSoldItems(int days) {

        LocalDate startDate = LocalDate.now().minusDays(days); // 현재날짜에서 30일(디폴트값)을 빼기 2025/02/11 - 30일 이런식으로 근 한달간 판매된 상품 조회

        // Object리스트엔 [상품id,총판매량] 이 들어있고 jpql 기반으로 인기 상품을 가져옴

        return soldItemRepository.findMostPopularSoldItems(startDate);
    }

}
