package com.example.onlinestore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateOrderRequest {

    @NotBlank(message = "Адрес доставки обязателен")
    private String shippingAddress;

    @NotBlank(message = "Номер телефона обязателен")
    private String phoneNumber;

}