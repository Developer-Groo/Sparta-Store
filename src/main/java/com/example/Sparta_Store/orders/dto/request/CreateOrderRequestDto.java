package com.example.Sparta_Store.orders.dto.request;

import com.example.Sparta_Store.address.entity.Address;

public record CreateOrderRequestDto(Address address, Long issuedCouponId) {

}
