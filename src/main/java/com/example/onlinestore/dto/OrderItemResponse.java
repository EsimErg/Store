package com.example.onlinestore.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long productId, String productName, BigDecimal productPrice, Integer quantity,
                                BigDecimal totalPrice) {

}