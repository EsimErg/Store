package com.example.onlinestore.dto;

import com.example.onlinestore.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentRequest {

    @NotNull(message = "ID заказа обязателен")
    private Long orderId;

    @NotNull(message = "Способ оплаты обязателен")
    private PaymentMethod method;

    // Только для учебной симуляции
    @NotNull(message = "Результат оплаты обязателен")
    private Boolean success;

}
