package com.example.Sparta_Store.orders;

import java.util.Arrays;
import java.util.Map;

public enum OrderStatus {
    ORDER_COMPLETED, // 주문완료
    ORDER_CANCEL_REQUEST, // 주문취소요청
    PREPARING_SHIPMENT, // 배송준비중
    SHIPPING, // 배송중
    DELIVERED, //배송완료
    CANCELED, //취소완료
    RETURN_REQUESTED, // 반품요청
    EXCHANGE_REQUESTED, // 교환요청
    RETURNED, // 반품완료
    EXCHANGED, // 교환완료
    CONFIRMED; // 구매확정

    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(orderStatus))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("등록되지 않은 주문상태 값입니다."));
    }

    public static Map<OrderStatus, OrderStatus> statusUpdatable = Map.of(
        ORDER_CANCEL_REQUEST, ORDER_COMPLETED,
        PREPARING_SHIPMENT, ORDER_COMPLETED,
        SHIPPING, PREPARING_SHIPMENT,
        DELIVERED, SHIPPING,
        CANCELED, ORDER_CANCEL_REQUEST,
        RETURN_REQUESTED, DELIVERED,
        EXCHANGE_REQUESTED, DELIVERED,
        CONFIRMED, DELIVERED,
        RETURNED, RETURN_REQUESTED,
        EXCHANGED, EXCHANGE_REQUESTED
    );

}
