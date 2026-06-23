package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCartItemRequest {

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть минимум 1")
    private Integer quantity;

}