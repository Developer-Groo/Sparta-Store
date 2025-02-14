package com.example.Sparta_Store.user.dto;

import com.example.Sparta_Store.address.entity.AddressDto;

public record UpdateInfoRequestDto(String name, AddressDto address) {

} //엔티티와 DTO 간의 책임 분리가 제대로 되지 않는 문제 /
// API 응답에서 불필요한 정보 포함 가능 → Address 엔티티에 추가적인 필드가 생기면, 필요하지 않은 데이터까지 외부에 노출될 수 있음
