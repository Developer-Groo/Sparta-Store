package com.example.Sparta_Store.orders.service;

import com.example.Sparta_Store.OrderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.OrderItem.service.OrderItemService;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.cartItem.service.CartItemService;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemService orderItemService;
    private final CartItemService cartItemService;

    /**
     * 주문 생성
     * - Cart 조회 -> CartItem 조회 -> Orders 생성 -> OrderItem 생성 -> CartItem 삭제
     */

    public void checkoutCart(Long userId){

        // 카트 조회
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
            () -> new IllegalArgumentException("카트 정보를 찾을 수 없습니다.")
        );
        Long cartId = cart.getId();

        // 카트에 상품이 담겨있어야 주문 생성 가능
        List<CartItem> cartItemList = cartItemRepository.findByCartId(cartId)
            .filter(list -> !list.isEmpty()) // 리스트가 비어있지 않은 경우만 반환
            .orElseThrow(() -> new IllegalArgumentException("장바구니에 상품이 없습니다.")
        );

        // Orders 엔티티 생성
        Long orderId = createOrder(userId);
        log.info("Orders 생성 완료");
        // OrderItem 엔티티 생성
        orderItemService.createOrderItem(orderId, cartItemList);
        log.info("OrderItem 생성 완료");
        // CartItem 초기화
        cartItemService.deleteCartItem(cartItemList);
        log.info("CartItem 초기화 완료");

    }

    @Transactional
    public Long createOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")
        );

        Orders savedOrder = new Orders(user, OrderStatus.주문완료);
        ordersRepository.save(savedOrder);
        // orderId 반환
        return savedOrder.getId();
    }

}
