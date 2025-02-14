package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.popularItem.entity.LikedItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface LikedItemRepository extends JpaRepository<LikedItem, Long> {
    @Query("SELECT l.item.id, COUNT(l) as like_count " + // likeitem 엔티티에서 좋아요 받은 상품의 id를 조회
            "FROM LikedItem l " + // likeditem 엔티티를 기준테이블로 설정
            "GROUP BY l.item.id " + // 같은 Item이 여러 개의 likedItem 레코드로 저장되어 있을 경우, 하나의 그룹으로 묶고 이 그룹을 기준으로 좋아요 받은 갯수를 계산
            "ORDER BY like_count DESC") // 좋아요 갯수 기준으로 내림차순 정렬
    List<Object[]> findMostLikedItems();
}
