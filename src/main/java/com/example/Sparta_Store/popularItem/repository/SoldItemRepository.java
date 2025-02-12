package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.popularItem.entity.SoldItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {
//    // 최근 N일 동안 판매된 상품 조회
//    @Query("SELECT s.item.id, SUM(s.soldQuantity) as total_sold " + // s.item.id 판매된 상품 id 선택 > SUM(s.soldQuantity) as total_sold 판매된 총 개수 계산
//            "FROM SoldItem s " + //SoldItem 테이블을 s 라는 별칭으로 명명
//            "WHERE s.soldAt >= :startDate " + // startDate를 정하고 이후 날짜인 SoldItem.soldAt 부터 조회하겠다.
//            "GROUP BY s.item.id " + // 같은 상품에 대한 데이터를 하나로 묶음 ex) 위에서 정한 날짜 내에 같은 아이템이 다른 날짜에 여러번 팔린 걸 한번에 팔렸다고 생각하겠단 뜻
//            "ORDER BY total_sold DESC") // 많이 판매된 순으로 조회
//    List<Object[]> findMostPopularSoldItems(@Param("startDate") LocalDate startDate); // startDate로 날짜를 정하고 인기상품조회 starDate = 현재 날짜

    @Query("SELECT i FROM Item i " +
            "JOIN SoldItem s ON i.id = s.item.id " +
            "WHERE s.soldAt >= :startDate " +
            "GROUP BY i " +
            "ORDER BY SUM(s.soldQuantity) DESC") // 판매량 내림차순 정렬 유지
    List<Item> findMostPopularItems(@Param("startDate") LocalDate startDate);
}
