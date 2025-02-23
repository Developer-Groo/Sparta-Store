package com.example.Sparta_Store.orders.service;

import static com.example.Sparta_Store.orders.OrderStatus.statusUpdatable;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.cart.repository.CartRepository;
import com.example.Sparta_Store.cart.service.CartService;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.orderItem.dto.response.OrderItemResponseDto;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.CreateOrderRequestDto;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.dto.response.OrderResponseDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Slf4j(topic = "OrderService")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ItemService itemService;

    /**
     * 주문서 페이지 -> 주문 생성 (결제전)
     */
    @Transactional
    public String checkoutOrder(Long userId, CreateOrderRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")
        );

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

        long totalPrice = getTotalPrice(cartItemList);

        Address address = requestDto == null ? user.getAddress() : requestDto.address();

        // order 엔티티 생성 호출
        String orderId = createOrder(userId, totalPrice, address);
        log.info("Orders 생성 완료");

        // orderItem 엔티티 생성 호출
        createOrderItem(orderId, cartItemList);
        log.info("OrderItem 생성 완료");

        return orderId;
    }

    public void getPaymentInfo(Model model, Long userId, String orderId) {
        // 요청을 보낸 user와 order 주인이 동일한지 검증
        // 주문서를 작성한 후, 로그인 정보가 바뀌었을 상황 대비
        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")
        );

        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        if(!order.getUser().equals(user)) {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }

        // model에 amount, quantity, orderName, customerEmail, customerName, customerKey 정보 추가
        List<OrderItem> orderItemList = orderItemRepository.findOrderItemsByOrders(order).orElseThrow(
            () -> new IllegalArgumentException("주문 상품 정보를 찾을 수 없습니다.")
        );

        long amount = order.getTotalPrice();
        int quantity = orderItemList.size();
        String orderName = orderItemList.get(0).getItem().getName();
        String customerEmail = user.getEmail();
        String customerName = user.getName();
        String customerKey = user.getCustomerKey();

        // 모델에 주문 정보를 추가
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        if(quantity == 1) {
            model.addAttribute("orderName", orderName);
        }
        else {
            model.addAttribute("orderName", orderName+" 외 "+(quantity-1)+"건");
        }
        model.addAttribute("quantity", quantity);
        model.addAttribute("customerEmail", customerEmail);
        model.addAttribute("customerName", customerName);
        model.addAttribute("customerKey", customerKey);
    }

    // orders 생성
    @Transactional
    public String createOrder(Long userId, long totalPrice, Address address) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다.")
        );

        Orders savedOrder = new Orders(user, totalPrice, address);
        ordersRepository.save(savedOrder);

        // orderId 반환
        return savedOrder.getId();
    }

    // orderItem 생성
    @Transactional
    public void createOrderItem(String orderId, List<CartItem> cartItemList) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        for (CartItem cartItem : cartItemList) {
            int orderPrice = (cartItem.getItem().getPrice()) * (cartItem.getQuantity());
            OrderItem savedOrderItem = new OrderItem(
                order,
                cartItem.getItem(),
                orderPrice,
                cartItem.getQuantity()
            );
            orderItemRepository.save(savedOrderItem);
        }
        ordersRepository.save(order);
    }

    // get totalPrice //TODO cartItem Repository
    public long getTotalPrice(List<CartItem> cartItemList) {
        long totalPrice = 0;

        for (CartItem cartItem : cartItemList) {
            int orderPrice = (cartItem.getItem().getPrice()) * (cartItem.getQuantity());
            totalPrice += orderPrice;
        }

        return totalPrice;
    }

    /**
     * 주문 상태 변경
     * - 주문취소는 주문완료 상태에서만 가능
     */
    @Transactional
    public void updateOrderStatus(Long userId, String orderId, UpdateOrderStatusDto requestDto) {

        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        if(!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("주문자와 유저 정보가 일치하지 않습니다.");
        }

        OrderStatus originStatus = order.getOrderStatus();

        OrderStatus requestStatus = OrderStatus.of(requestDto.orderStatus());

        isStatusUpdatable(originStatus, requestStatus); // 주문상태 변경 가능 여부

        order.updateOrderStatus(requestStatus);
        log.info("주문상태 변경 완료 >> {}", requestDto.orderStatus());

    }

    // 주문상태 변경 가능 여부
    public void isStatusUpdatable(OrderStatus originStatus, OrderStatus requestStatus) {
        if (requestStatus != OrderStatus.ORDER_CANCEL_REQUEST
            && requestStatus != OrderStatus.RETURN_REQUESTED
            && requestStatus != OrderStatus.EXCHANGE_REQUESTED) {
            throw new IllegalArgumentException("주문상태 변경 권한이 없습니다.");
        }

        if (!statusUpdatable.get(requestStatus).equals(originStatus)) {
            throw new IllegalArgumentException(
                String.format("'%s' 상태에서는 '%s' 상태로 변경할 수 없습니다.", originStatus, requestStatus)
            );
        }
    }

    /**
     * 자동 구매확정
     * - 주문상태가 "DELIVERED" 이며, 업데이트일자가 5일 전인 주문의 상태를 구매확정으로 바꾼다.
     * - 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoConfirmOrders() {
        // 조건에 맞는 주문 리스트 조회
        List<Orders> orderList = ordersRepository.findOrdersForAutoConfirmation();

        // 주문상태 변경
        orderList.forEach(orders -> orders.updateOrderStatus(OrderStatus.CONFIRMED));
        log.info("{} 기준, 총 {}개의 주문을 자동 구매확정 하였습니다. ", LocalDateTime.now(), orderList.size());
    }

    /**
     * 주문 리스트 조회
     * - orders 조회
     */
    public PageResult<OrderResponseDto> getOrders(Long userId, PageQuery pageQuery) {
        Page<OrderResponseDto> orderList = ordersRepository.findByUserId(userId, pageQuery.toPageable())
            .map(OrderResponseDto::toDto);

        return PageResult.from(orderList);
    }

    /**
     * 주문 내역 상세 조회
     * - orderItem 조회
     */
    public PageResult<OrderItemResponseDto> getOrderItems(
        Long userId,
        String orderId,
        PageQuery pageQuery
    ) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        if(!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("주문자와 유저 정보가 일치하지 않습니다.");
        }

        Page<OrderItemResponseDto> orderItemList = orderItemRepository.findByOrderId(orderId, pageQuery.toPageable())
            .map(OrderItemResponseDto::toDto);

        return PageResult.from(orderItemList);
    }

}
