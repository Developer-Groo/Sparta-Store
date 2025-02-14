package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.popularItem.entity.SoldItem;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {

@Query("SELECT i.id, SUM(s.soldQuantity) " + // id 와 수량의합만 가져옴
        "FROM Item i " + // item 엔티티를 기준으로 조회
        "JOIN SoldItem s ON i.id = s.item.id " + // item 테이블 과 join테이블 조인 > solditem의 item id 와 item 의 id가 같은 데이터 조회 / 한 번도 판매되지 않은 상품은 결과에서 제외
        "WHERE s.soldAt >= :startDate " + // 특정 날짜(startDate) 이후에 판매된 상품만 조회
        "GROUP BY i.id " + //같은 Item이 여러 개의 SoldItem 레코드로 저장되어 있을 경우, 하나의 그룹으로 묶고 이 그룹을 기준으로 SUM(s.soldQuantity)를 계산
        "ORDER BY SUM(s.soldQuantity) DESC") // 많이 팔린 순으로 정렬
List<Object[]> findMostPopularSoldItems(@Param("startDate") LocalDate startDate);

}
