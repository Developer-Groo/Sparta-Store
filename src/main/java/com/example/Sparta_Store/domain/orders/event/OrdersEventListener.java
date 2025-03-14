package com.example.Sparta_Store.domain.orders.event;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.domain.orderItem.entity.OrderItem;
import com.example.Sparta_Store.domain.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.domain.orders.entity.Orders;
import com.example.Sparta_Store.domain.orders.exception.OrdersErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "OrdersEventListener")
@Component
@RequiredArgsConstructor
public class OrdersEventListener {

    private final OrderItemRepository orderItemRepository;

    // 주문아이템 삭제
    @Async
    @EventListener
    public void deleteOrderItems(OrdersPaymentCancelledEvent event) {
        List<Orders> orderList = event.getOrdersList();
        int cnt = 0;

        for (Orders order : orderList) {
            try {
                deleteOrderItems(order.getId());
            } catch (Exception e) {
                cnt++;
                log.error("주문 {}의 주문아이템 삭제 실패: {}", order.getId(), e.getMessage());
            }
        }
        log.info("{}개 주문의 주문아이템을 삭제하였습니다. 실패 주문 건수: {}개", orderList.size()-cnt, cnt);
    }

    @Transactional
    void deleteOrderItems(String orderId) {
        orderItemRepository.deleteOrderItemsByOrderId(orderId);
    }

    // 아이템 재고 복구
    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void undoStockForCancelledOrder(OrderCancelledEvent event) {
        try {
            List<OrderItem> orderItemList = orderItemRepository.findOrderItemsByOrders(event.order())
                .orElseThrow(() -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER_ITEM));

            for (OrderItem orderItem : orderItemList) {
                orderItem.getItem().increaseStock(orderItem.getQuantity());
                log.info("재고 복구 완료(itemId: {}, 복구한 재고 수: {})", orderItem.getItem().getId(), orderItem.getQuantity());
            }

        } catch (Exception e) {
            log.error("재고 복구 실패: {}", e.getMessage());
        }
    }

}
