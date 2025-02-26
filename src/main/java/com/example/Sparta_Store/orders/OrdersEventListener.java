package com.example.Sparta_Store.orders;

import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.entity.Orders;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "OrdersEventListener")
@Component
@RequiredArgsConstructor
public class OrdersEventListener {

    private final OrderItemRepository orderItemRepository;

    @Async
    @EventListener
    public void deleteOrderItems(OrdersPaymentCancelledEvent event) {
        List<Orders> orderList = event.getOrdersList();
        int cnt = 0;

        for (Orders order : orderList) {
            try {
                // 주문당 개별 트랜잭션
                deleteOrderItems(order.getId());
            } catch (Exception e) {
                cnt++;
                log.error("주문 {}의 주문아이템 삭제 실패: {}", order.getId(), e.getMessage());
            }
        }

        log.info("{}개 주문의 주문아이템을 삭제하였습니다. 실패 주문 건수: {}개", orderList.size()-cnt, cnt);
    }

    @Transactional
    public void deleteOrderItems(String orderId) {
        orderItemRepository.deleteOrderItemsByOrderId(orderId);
    }
}
