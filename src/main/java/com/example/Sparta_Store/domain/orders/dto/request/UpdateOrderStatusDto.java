package com.example.Sparta_Store.domain.orders.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusDto(@NotBlank String orderStatus) {

}
