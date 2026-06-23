package com.example.onlinestore.dto;

import com.example.onlinestore.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateOrderStatusRequest {

    @NotNull(message = "Статус заказа обязателен")
    private OrderStatus status;

}