package com.example.Sparta_Store.domain.orders.dto.request;

import com.example.Sparta_Store.domain.address.entity.Address;

public record CreateOrderRequestDto(Address address, Long issuedCouponId) {

}
