package com.example.Sparta_Store.orders;

import com.example.Sparta_Store.orders.entity.Orders;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrdersPaymentCancelledEvent {
    private final List<Orders> ordersList;
}
