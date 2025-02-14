package com.example.Sparta_Store.popularItem.repository.liked;

import com.example.Sparta_Store.popularItem.entity.LikedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedItemRepository extends JpaRepository<LikedItem, Long>, LikedItemRepositoryCustom {
    // JPA 기본 인터페이스 담당 > 기본적인 crud 사용가능  // 커스텀을 상속
}
