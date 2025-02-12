package com.example.Sparta_Store.OrderItem.controller;

import com.example.Sparta_Store.OrderItem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
}
