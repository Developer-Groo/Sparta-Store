package com.example.Sparta_Store.admin.orders.controller;

import com.example.Sparta_Store.admin.orders.service.AdminOrderService;
import com.example.Sparta_Store.orders.dto.request.UpdateOrderStatusDto;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    /**
     * 관리자 주문 상태 변경
     */
    @PatchMapping("status/{orderId}")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
        @PathVariable("orderId") String orderId,
        @Valid @RequestBody UpdateOrderStatusDto requestDto
    ) {
        adminOrderService.updateOrderStatus(orderId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "주문상태 변경이 완료되었습니다."));
    }
}
