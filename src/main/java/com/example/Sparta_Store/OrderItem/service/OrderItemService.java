package com.example.Sparta_Store.OrderItem.service;

import com.example.Sparta_Store.OrderItem.entity.OrderItem;
import com.example.Sparta_Store.OrderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public void createOrderItem(Long orderId, List<CartItem> cartItemList){
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        int totalPrice = 0;

        for(CartItem cartItem:cartItemList){
            int orderPrice = (cartItem.getItem().getPrice())*(cartItem.getQuantity());
            totalPrice+=orderPrice;
            OrderItem savedOrderItem = new OrderItem(order, cartItem.getItem(), orderPrice, cartItem.getQuantity());
            orderItemRepository.save(savedOrderItem);
        }
        order.setTotalPrice(totalPrice);
        ordersRepository.save(order);
    }

}
