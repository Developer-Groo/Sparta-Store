package com.example.Sparta_Store.orders.service;

import static com.example.Sparta_Store.orders.OrderStatus.statusUpdatable;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.cart.service.CartRedisService;
import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.orderItem.dto.response.OrderItemResponseDto;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.dto.request.CreateOrderRequestDto;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.dto.response.OrderResponseDto;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.event.OrderConfirmedEvent;
import com.example.Sparta_Store.orders.exception.OrdersErrorCode;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.user.repository.UserRepository;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final CartRedisService cartRedisService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void getPaymentInfo(Model model, Long userId, String orderId)
        throws JsonProcessingException {
        // 요청을 보낸 user와 order 주인이 동일한지 검증
        // 주문서를 작성한 후, 로그인 정보가 바뀌었을 상황 대비
        Users user = userRepository.findById(userId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_USER)
        );

        String key = getOrderKey(orderId);
        String orderJson = (String) redisTemplate.opsForHash().get(key, "order");
        Orders order = objectMapper.readValue(orderJson, Orders.class);

        if (order == null) {
            throw new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER);
        }

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(OrdersErrorCode.USER_MISMATCH);
        }

        List<Object> objectList = redisTemplate.opsForList().range(getOrderItemKey(orderId), 0, -1);

        List<OrderItem> orderItemList = objectList.stream()
            .map(obj -> {
                try {
                    String json = obj.toString();  // Object → String 변환
                    return objectMapper.readValue(json, OrderItem.class); // JSON → OrderItem 변환
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Redis에서 OrderItem 변환 실패", e);
                }
            })
            .toList();



        long amount = order.getTotalPrice();
        int quantity = orderItemList.size();
        String orderName = (orderItemList.get(0)).getItem().getName();
        String customerEmail = user.getEmail();
        String customerName = user.getName();
        String customerKey = user.getCustomerKey();

        // 모델에 주문 정보를 추가
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        if (quantity == 1) {
            model.addAttribute("orderName", orderName);
        } else {
            model.addAttribute("orderName", orderName + " 외 " + (quantity - 1) + "건");
        }
        model.addAttribute("quantity", quantity);
        model.addAttribute("customerEmail", customerEmail);
        model.addAttribute("customerName", customerName);
        model.addAttribute("customerKey", customerKey);
    }

    /**
     * 주문서 페이지 -> Redis 임시 주문 생성 (결제전)
     */
    // Orders , OrderItem 생성
    @Transactional
    public String checkoutOrder(Long userId, CreateOrderRequestDto requestDto) {

        List<CartItem> cartItemList = cartRedisService.getCartItemList(userId);
        // order 엔티티 생성 호출
        Orders order = createOrder(userId, requestDto);
        log.info("Orders 생성 완료");

        // orderItem 엔티티 생성 호출
        createOrderItem(order, cartItemList);
        log.info("OrderItems 생성 완료");

        return order.getId();
    }

    // Redis order 생성 (임시 주문)
    @Transactional
    public Orders createOrder(Long userId, CreateOrderRequestDto requestDto) {
        Users user = userRepository.findById(userId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_USER)
        );
        List<CartItem> cartItemList = cartRedisService.getCartItemList(userId);

        if (cartItemList.isEmpty()) {
            throw new CustomException(OrdersErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        long totalPrice = cartRedisService.getTotalPrice(cartItemList);
        Address address = requestDto == null ? user.getAddress() : requestDto.address();

        Orders order = new Orders(user, totalPrice, address);
        String key = getOrderKey(order.getId());
        try { // 직렬화
            String orderJson = objectMapper.writeValueAsString(order);
            redisTemplate.opsForHash().put(key, "order", orderJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Order 객체 JSON 변환 실패", e);
        }

        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        redisTemplate.opsForHash().put(key, "createdAt", createdAt);

        redisTemplate.expire(key, 10, TimeUnit.MINUTES);

        // order 반환
        return order;
    }
    
    // Redis orderItem 생성 (임시 주문아이템)
    @Transactional
    public void createOrderItem(Orders order, List<CartItem> cartItemList) {

        if (cartItemList.isEmpty()) {
            throw new CustomException(OrdersErrorCode.NOT_EXISTS_CART_PRODUCT);
        }

        List<OrderItem> orderItemList = cartItemList.stream()
                .map(cartItem -> new OrderItem(
                    order,
                    cartItem.getItem(),
                    (cartItem.getItem().getPrice()) * (cartItem.getQuantity()),
                    cartItem.getQuantity())
                ).toList();

        String key = "order:" + order.getId() + ":items";

        orderItemList.forEach(orderItem -> {
            try { // 직렬화
                String json = objectMapper.writeValueAsString(orderItem);
                redisTemplate.opsForList().rightPush(key, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Redis에 OrderItem 저장 실패", e);
            }
        });

        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
    }

    // MySQL orders 생성 (결제 완료된 주문)
    @Transactional
    public void mysqlOrder(Orders order) {
        ordersRepository.save(order);
    }

    // MySQL orderItem 생성 (결제 완료된 주문)
    @Transactional
    public void mysqlOrderItem(String orderId) {
        String key = getOrderItemKey(orderId);

        List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);

        List<OrderItem> orderItemList = objectList.stream()
            .map(obj -> {
                try { // 역직렬화
                    String json = obj.toString();
                    return objectMapper.readValue(json, OrderItem.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Redis에서 OrderItem 변환 실패", e);
                }
            })
            .toList();

        if (orderItemList.isEmpty()) {
            throw new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER_ITEM);
        }

        // 3. MySQL에 저장
        orderItemRepository.saveAll(orderItemList);
    }

    /**
     * 주문 상태 변경 - 주문취소는 주문완료 상태에서만 가능
     */
    @Transactional
    public void updateOrderStatus(Long userId, String orderId, UpdateOrderStatusDto requestDto) {

        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER)
        );

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(OrdersErrorCode.USER_MISMATCH);
        }

        OrderStatus originStatus = order.getOrderStatus();
        OrderStatus requestStatus = OrderStatus.of(requestDto.orderStatus());

        isStatusUpdatable(originStatus, requestStatus); // 주문상태 변경 가능 여부
        order.updateOrderStatus(requestStatus);

        // 구매확정으로 변경 시 이벤트 발생
        if (requestStatus == OrderStatus.CONFIRMED) {
            eventPublisher.publishEvent(new OrderConfirmedEvent(userId, order.getTotalPrice()));
        }

        log.info("주문상태 변경 완료 >> {}: {}", orderId, requestDto.orderStatus());
    }

    // 주문상태 변경 가능 여부
    public void isStatusUpdatable(OrderStatus originStatus, OrderStatus requestStatus) {
        if (requestStatus != OrderStatus.ORDER_CANCEL_REQUEST
            && requestStatus != OrderStatus.RETURN_REQUESTED
            && requestStatus != OrderStatus.EXCHANGE_REQUESTED
            && requestStatus != OrderStatus.CONFIRMED) {
            throw new CustomException(OrdersErrorCode.ORDER_STATUS_CHANGE_FORBIDDEN);
        }

        if (!statusUpdatable.get(requestStatus).equals(originStatus)) {
            log.info("{} 상태에서는 {} 상태로 변경할 수 없습니다. ", originStatus, requestStatus);
            throw new CustomException(OrdersErrorCode.ORDER_STATUS_INVALID_TRANSITION);

        }
    }

    /**
     * 자동 구매확정 - 주문상태가 "DELIVERED" 이며, 업데이트일자가 5일 전인 주문의 상태를 구매확정으로 바꾼다. - 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoConfirmOrders() {
        // 조건에 맞는 주문 리스트 조회
        List<Orders> orderList = ordersRepository.findOrdersForAutoConfirmation();

        if (!orderList.isEmpty()) {
            // 주문상태 변경
            orderList.forEach(orders -> orders.updateOrderStatus(OrderStatus.CONFIRMED));
            log.info("{} 기준, 총 {}개의 주문을 자동 구매확정 하였습니다. ", LocalDateTime.now(), orderList.size());
        }

    }

    /**
     * 주문 리스트 조회 - orders 조회
     */
    public PageResult<OrderResponseDto> getOrders(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        OrderStatus orderStatus,
        PageQuery pageQuery
    ) {
        LocalDateTime startOfDay = (startDate != null)? startDate.atStartOfDay() : null;
        LocalDateTime endOfDay = (endDate != null)? endDate.atTime(23,59,59,999999) : null;

        Page<OrderResponseDto> orderList = ordersRepository.findOrders(
                userId,
                startOfDay,
                endOfDay,
                orderStatus,
                pageQuery.toPageable()
            )
            .map(OrderResponseDto::toDto);

        return PageResult.from(orderList);
    }

    /**
     * 주문 내역 상세 조회 - orderItem 조회
     */
    public PageResult<OrderItemResponseDto> getOrderItems(
        Long userId,
        String orderId,
        PageQuery pageQuery
    ) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER)
        );

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(OrdersErrorCode.USER_MISMATCH);
        }

        Page<OrderItemResponseDto> orderItemList = orderItemRepository.findByOrderId(orderId,
                pageQuery.toPageable())
            .map(OrderItemResponseDto::toDto);

        return PageResult.from(orderItemList);
    }

    // 상품 재고 감소
    @Transactional
    public void completeOrder(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER)
        );

        // 상품 재고 감소
        List<OrderItem> orderItemList = orderItemRepository.findOrderItemsByOrders(order)
            .orElseThrow(
                () -> new CustomException(OrdersErrorCode.NOT_EXISTS_ORDER_ITEM)
            );

        itemService.decreaseStock(orderItemList);
        log.info("상품 재고 감소 완료");
    }

    // Redis get order key
    public String getOrderKey(String orderId) {
        return "order:" + orderId;
    }

    public String getOrderItemKey(String orderId) {
        return "order:" + orderId + ":items";
    }
}
