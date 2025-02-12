package com.example.Sparta_Store.OrderItem.controller;

import com.example.Sparta_Store.OrderItem.dto.response.OrderItemResponseDto;
import com.example.Sparta_Store.OrderItem.service.OrderItemService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * 주문 내역 상세 조회 API
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PageResult<OrderItemResponseDto>> getOrderDetail(
        PageQuery pageQuery, @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(orderItemService.getOrderItems(orderId, pageQuery));
    }
}
