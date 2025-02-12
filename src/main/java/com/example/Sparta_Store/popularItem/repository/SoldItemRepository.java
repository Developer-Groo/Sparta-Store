package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.popularItem.entity.SoldItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {
    @Query("SELECT i FROM Item i " + // 아이템 엔티티를 그대로 조회하는 쿼리 위 코드에선 id만 조회 지금은 item 자체를 반환
            "JOIN SoldItem s ON i.id = s.item.id " + // item 과 sold item 조인 >> 두개를 함께 조회 가능
            "WHERE s.soldAt >= :startDate " + // startDate 기준으로 이후에 판매된 데이터만 조회
            "GROUP BY i " + //같은 상품에 대한 데이터를 하나로 묶음 ex) 위에서 정한 날짜 내에 같은 아이템이 다른 날짜에 여러번 팔린 걸 한번에 팔렸다고 생각하겠단 뜻
            "ORDER BY SUM(s.soldQuantity) DESC") // 총 판매량을 합치고 계산한 뒤 내림차순으로 정렬 // 패치 조인을 통해 데이터가 많아질 경우 성능 저하 방지
    List<Item> findMostPopularSoldItems(@Param("startDate") LocalDate startDate);
}
