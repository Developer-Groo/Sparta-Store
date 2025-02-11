package com.example.Sparta_Store.orders;

import java.util.Arrays;

public enum OrderStatus {
    주문완료, 주문취소요청, 배송준비중, 배송중, 배송완료, 취소완료,
    반품요청, 교환요청, 반품완료, 교환완료, 구매확정;

    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(orderStatus))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("등록되지 않은 주문상태 값입니다."));
    }
}
