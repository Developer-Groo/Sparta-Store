package com.example.Sparta_Store.popularItem.dto;

public record PopularItemRankValueDto(Long itemId, int rankValue) {

    // List<Object[]> 이방식은 유지보수가 어렵다. 기존에는 id를 받기위해 row[0] 이런식으로 사용했음
    // 코드를 수정하기 위해 보면 한 눈에 알아채기 어려움
    // row[0], row[1] 대신 PopularItemRankDto.itemId(), PopularItemRankDto.rankValue() 사용

}
