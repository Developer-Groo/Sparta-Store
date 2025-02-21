package com.example.Sparta_Store.popularItem.repository.sold;

import static com.example.Sparta_Store.item.entity.QItem.item;
import static com.example.Sparta_Store.popularItem.service.PopularItemService.soldItemToDto;

import com.example.Sparta_Store.item.entity.QItem;
import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoldItemRepositoryImpl implements SoldItemRepositoryCustom {

    private final JPAQueryFactory queryFactory; // QureyDSL을 실행하는 객체

    @Override
    public List<PopularItemRankValueDto> findMostPopularSoldItems() { //customRepository에서 findMostPopularSoldItems 구현

        LocalDateTime currentWeekStartDate = LocalDateTime.now().with(DayOfWeek.MONDAY);

        //  QueryDSL을 사용하여 데이터 조회 List<Tuple> 형태로 반환
        List<Tuple> idAndSalesNums = queryFactory
                .select(item.id, item.salesSummary.totalSales) // id와 판매량 조회
                .from(item) // 판매 기록이 저장된 Item을 조회 대상으로 지정
                .leftJoin(item.salesSummary) //  판매 요약 데이터와 조인 (1:1 관계)
                .where(QItem.item.salesSummary.createdAt.goe(currentWeekStartDate)) // ✅ 최근 주간 데이터만 조회
                .orderBy(item.salesSummary.totalSales.desc()) // 많이 팔린 순으로 내림차순 정렬
                .limit(10) // 상위10개
                .fetch(); //쿼리 실행시 List<Tuple> 리스트 형태로 결과를 반환 > List<Tuple> 이 튜플에는 상품id와 판매량이 들어있음

        return soldItemToDto(idAndSalesNums); // DTO 변환 실행

    }

}



 // 서비스로 옮기기

