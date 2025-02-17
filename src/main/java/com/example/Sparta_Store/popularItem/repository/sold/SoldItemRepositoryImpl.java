package com.example.Sparta_Store.popularItem.repository.sold;

import com.example.Sparta_Store.item.entity.QItem;
import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import com.example.Sparta_Store.popularItem.entity.QSoldItem;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoldItemRepositoryImpl implements SoldItemRepositoryCustom {

    private final JPAQueryFactory queryFactory; // QureyDSL을 실행하는 객체

    @Override
    public List<PopularItemRankValueDto> findMostPopularSoldItems(LocalDate startDate) { //customRepository에서 findMostPopularSoldItems 구현

        QSoldItem soldItem = QSoldItem.soldItem; // soldItem으로 soldItem엔티티에 접근 가능
        QItem item = QItem.item; // soldItem으로만 접근하기에는 item정보가 부족해서 QItem으로 정보를 가져옴

        // QueryDSL을 사용하여 데이터 조회 (List<Tuple> 형태로 반환)
        List<Tuple> idAndSoldNums = queryFactory
                .select(item.id, soldItem.soldQuantity.sum()) // id와 판매량 조회
                .from(soldItem) // 판매 기록이 저장된 SoldItem을 조회 대상으로 지정
                .join(soldItem.item, item) // item 테이블에 item 상세정보가 있으니까 solditem과 item을 조인
                .where(soldItem.soldAt.goe(startDate)) // startDate 이후 판매된 데이터만 조
                .groupBy(item.id) // 같은 상품이 여러 번 판매되었더라도, 판매량을 합산하기 위해 사용
                .orderBy(soldItem.soldQuantity.sum().desc()) // 많이 팔린 순으로 내림차순 정렬
                .fetch(); //쿼리 실행시 List<Tuple> 리스트 형태로 결과를 반환 > List<Tuple> 이 튜플에는 상품id와 판매량이 들어있음

        return toDto(idAndSoldNums); // dto로 변환하는 메서드 실행

    }

    // DTO 변환을 별도로 처리하는 메서드
    private List<PopularItemRankValueDto> toDto(List<Tuple> idAndSoldNums) {
        return idAndSoldNums.stream()
                .map(tuple -> new PopularItemRankValueDto(
                        tuple.get(QItem.item.id),
                        tuple.get(QSoldItem.soldItem.soldQuantity.sum()).intValue()
                ))
                .collect(Collectors.toList());
    }

}
