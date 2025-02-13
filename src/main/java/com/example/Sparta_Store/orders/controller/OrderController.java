package com.example.Sparta_Store.orders.controller;

import com.example.Sparta_Store.config.jwt.JwtUtil;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import com.example.Sparta_Store.orders.dto.response.OrderResponseDto;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    /**
     * 주문 생성 API
     * - 장바구니에서 '결제하기'를 누르면 동작
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String,String>> createOrder(HttpServletRequest request) {
        Long userId = getUserId(request);

        orderService.checkoutCart(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "상품 주문이 완료되었습니다."));
    }

    /**
     * 주문 상태 변경 API
     */
    @PatchMapping("status/{orderId}")
    public ResponseEntity<Map<String,String>> updateOrderStatus(
        HttpServletRequest request,
        @PathVariable("orderId") Long orderId,
        @Valid @RequestBody UpdateOrderStatusDto requestDto
    ) {
        Long userId = getUserId(request);

        orderService.updateOrderStatus(userId, orderId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "주문상태 변경이 완료되었습니다."));
    }

    /**
     * 주문 리스트 조회 API
     */
    @GetMapping()
    public ResponseEntity<PageResult<OrderResponseDto>> getOrders(HttpServletRequest request, PageQuery pageQuery) {
        Long userId = getUserId(request);

        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(userId, pageQuery));
    }

    // jwt에 저장되어 있는 userId 추출 메서드
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return jwtUtil.extractId(token);
    }

}
