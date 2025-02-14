package com.example.Sparta_Store.popularItem.repository.liked;

import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import com.example.Sparta_Store.popularItem.entity.QLikedItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikedItemRepositoryImpl implements LikedItemRepositoryCustom {

    private final JPAQueryFactory queryFactory; // QueryDSL을 실행하는 객체

    @Override
    public List<PopularItemRankValueDto> findMostLikedItems() { // customRepository에서 findMostLikedItems 메서드 구현

        QLikedItem likedItem = QLikedItem.likedItem; //likedItem으로 LikedItem엔티티에 접근 가능 // QLikedItem QueryDSL을 사용할때 쓰는 컨벤션인듯?

        return queryFactory
                .select(likedItem.item.id, likedItem.count()) //LikedItem 엔티티의 item.id를 조회 , LikedItem 엔티티에서 특정 item.id에 대한 찜 갯수 를 조회
                .from(likedItem) //LikedItem 엔티티를 조회 대상으로 지정
                .groupBy(likedItem.item.id) // groupBy를 통해 다른 날짜에 같은 상품에 다른 사용자가 찜을 해도 좋아요 개수는 합쳐져야 하니까 그룹으로 묶을 수 있음
                .orderBy(likedItem.count().desc()) // 찜 갯수로 내림차순 정렬
                .fetch() // 쿼리 실행시 List<Tuple> 리스트 형태로 결과를 반환 > List<Tuple> 이 튜플에는 상품id와 좋아요 갯수가 들어있음
                .stream() // 위에 List<Tuple> 이형태를 자바스트림으로 변환
                .map(IdAndLikedNum -> new PopularItemRankValueDto(IdAndLikedNum.get(likedItem.item.id), IdAndLikedNum.get(likedItem.count()).intValue()))
                // ㄴ IdAndLikedNum 객체 에서 item.id와 좋아요 갯수를 추출하고 PopularItemRankValueDto 타입의 dto로 변환
                .collect(Collectors.toList()); // 위에 변환한 dto를 List<PopularItemRankValueDto> 로 변환
    }
    // 쿼리문 메서드
}
