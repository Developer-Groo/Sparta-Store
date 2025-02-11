package com.example.Sparta_Store.orders.controller;

import com.example.Sparta_Store.orders.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 API
     * - 장바구니에서 '결제하기'를 누르면 동작
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String,String>> createOrder(
        HttpServletRequest request
    ){
//        String token = request.getHeader("Authorization");

        Long userId = 1L;
        orderService.checkoutCart(userId);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(Map.of("message", "상품 주문이 완료되었습니다."));
    }

    /**
     * 주문 상태 변경 API
     */

    /**
     * 주문 리스트 조회 API
     */

    /**
     * 주문 내역 상세 조회 API
     */

}
