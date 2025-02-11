package com.example.Sparta_Store.orders.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusDto(@NotBlank String orderStatus) {

}
