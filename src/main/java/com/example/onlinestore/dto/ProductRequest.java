package com.example.onlinestore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

    @Getter
    public class ProductRequest {

        @NotBlank(message = "Название товара обязательно")
        private String name;

        private String description;

        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
        private BigDecimal price;

        @NotNull(message = "Количество обязательно")
        @Min(value = 0, message = "Количество не может быть меньше 0")
        private Integer stockQuantity;

        @NotNull(message = "Категория обязательна")
        private Long categoryId;

    }

