package com.example.Sparta_Store.popularItem.repository.liked;

import com.example.Sparta_Store.popularItem.dto.PopularItemRankValueDto;
import java.util.List;

public interface LikedItemRepositoryCustom {
    List<PopularItemRankValueDto> findMostLikedItems();
    // QueryDsl을 사용하겠다
}
