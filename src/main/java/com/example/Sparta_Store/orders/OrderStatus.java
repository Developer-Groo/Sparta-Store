package com.example.Sparta_Store.orders;

import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    BEFORE_PAYMENT("결제전"),
    PAYMENT_CANCELLED("결제취소"),
    ORDER_COMPLETED("주문완료"),
    ORDER_CANCEL_REQUEST("주문취소요청"),
    PREPARING_SHIPMENT("배송준비중"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELED("취소완료"),
    RETURN_REQUESTED("반품요청"),
    EXCHANGE_REQUESTED("교환요청"),
    RETURNED("반품완료"),
    EXCHANGED("교환완료"),
    CONFIRMED("구매확정");

    private final String description;

    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(orderStatus))
            .findFirst()
            .orElseThrow(()-> new IllegalArgumentException("등록되지 않은 주문상태 값입니다."));
    }

    public static Map<OrderStatus, OrderStatus> statusUpdatable = Map.ofEntries(
        Map.entry(ORDER_CANCEL_REQUEST, ORDER_COMPLETED),
        Map.entry(PREPARING_SHIPMENT, ORDER_COMPLETED),
        Map.entry(SHIPPING, PREPARING_SHIPMENT),
        Map.entry(DELIVERED, SHIPPING),
        Map.entry(CANCELED, ORDER_CANCEL_REQUEST),
        Map.entry(RETURN_REQUESTED, DELIVERED),
        Map.entry(EXCHANGE_REQUESTED, DELIVERED),
        Map.entry(CONFIRMED, DELIVERED),
        Map.entry(RETURNED, RETURN_REQUESTED),
        Map.entry(EXCHANGED, EXCHANGE_REQUESTED),
        Map.entry(PAYMENT_CANCELLED, BEFORE_PAYMENT),
        Map.entry(ORDER_COMPLETED, BEFORE_PAYMENT)
    );

}
