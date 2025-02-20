package com.example.Sparta_Store.admin.orders.service;

import static com.example.Sparta_Store.orders.OrderStatus.statusUpdatable;

import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "AdminOrderService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrdersRepository ordersRepository;

    /**
     * 주문 상태 변경
     */
    @Transactional
    public void updateOrderStatus(String orderId, UpdateOrderStatusDto requestDto) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        OrderStatus originStatus = order.getOrderStatus();
        OrderStatus requestStatus = OrderStatus.of(requestDto.orderStatus());

        isStatusUpdatable(originStatus, requestStatus); // 주문상태 변경 가능 여부

        order.updateOrderStatus(requestStatus);
        log.info("주문상태 변경 완료 >> {}", requestDto.orderStatus());
    }

    // 주문상태 변경 가능 여부
    public void isStatusUpdatable(OrderStatus originStatus, OrderStatus requestStatus) {
        if(requestStatus == OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("주문완료 상태로 변경할 수 없습니다.");
        }

        if (!statusUpdatable.get(requestStatus).equals(originStatus)) {
            throw new IllegalArgumentException(
                String.format("'%s' 상태에서는 '%s' 상태로 변경할 수 없습니다.", originStatus, requestStatus)
            );
        }
    }
}
