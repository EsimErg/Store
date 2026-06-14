package com.example.onlinestore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequest {

    @NotBlank(message = "Название категории обязательно")
    private String name;
}
