package com.example.Sparta_Store.orders.service;

import static com.example.Sparta_Store.orders.OrderStatus.교환요청;
import static com.example.Sparta_Store.orders.OrderStatus.반품요청;
import static com.example.Sparta_Store.orders.OrderStatus.배송완료;
import static com.example.Sparta_Store.orders.OrderStatus.배송준비중;
import static com.example.Sparta_Store.orders.OrderStatus.배송중;
import static com.example.Sparta_Store.orders.OrderStatus.주문완료;
import static com.example.Sparta_Store.orders.OrderStatus.주문취소요청;

import com.example.Sparta_Store.OrderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.OrderItem.service.OrderItemService;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.cartItem.service.CartItemService;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.dto.response.OrderResponseDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrderQueryRepository;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final OrderQueryRepository orderQueryRepository;

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

        Orders savedOrder = new Orders(user, 주문완료);
        ordersRepository.save(savedOrder);
        // orderId 반환
        return savedOrder.getId();
    }

    /**
     * 주문 상태 변경
     * - 주문취소는 주문완료 상태에서만 가능
     */
    @Transactional
    public void updateOrderStatus(Long orderId, UpdateOrderStatusDto requestDto) {
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
        switch (requestStatus){
            case 주문완료 -> throw new IllegalArgumentException("주문완료 상태로 변경할 수 없습니다.");
            case 주문취소요청, 배송준비중 -> {
                if (!originStatus.equals(주문완료)) {
                    throw new IllegalArgumentException("주문완료 상태에서만 가능합니다.");
                }
            }
            case 배송중 -> {
                if (!originStatus.equals(배송준비중)) {
                    throw new IllegalArgumentException("배송중은 배송준비중 상태에서만 가능합니다.");
                }
            }
            case 배송완료 -> {
                if (!originStatus.equals(배송중)) {
                    throw new IllegalArgumentException("배송완료는 배송중 상태에서만 가능합니다.");
                }
            }
            case 취소완료 -> {
                if (!originStatus.equals(주문취소요청)) {
                    throw new IllegalArgumentException("취소완료는 주문취소요청 상태에서만 가능합니다.");
                }
            }
            case 반품요청, 교환요청, 구매확정 -> {
                if (!originStatus.equals(배송완료)) {
                    throw new IllegalArgumentException("반품요청, 교환요청, 구매확정은 배송완료 상태에서만 가능합니다.");
                }
            }
            case 반품완료 -> {
                if (!originStatus.equals(반품요청)) {
                    throw new IllegalArgumentException("반품완료는 반품요청 상태에서만 가능합니다.");
                }
            }
            case 교환완료 -> {
                if (!originStatus.equals(교환요청)) {
                    throw new IllegalArgumentException("교환완료는 교환요청 상태에서만 가능합니다.");
                }
            }
        }
    }

    /**
     * 주문 리스트 조회
     */
    public PageResult<OrderResponseDto> getOrders(Long userId, PageQuery pageQuery) {
        Page<OrderResponseDto> orderList = orderQueryRepository.findByUserId(userId, pageQuery.toPageable())
            .map(OrderResponseDto::toDto);

        return PageResult.from(orderList);
    }


}
