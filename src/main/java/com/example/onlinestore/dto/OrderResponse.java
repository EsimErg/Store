package com.example.onlinestore.dto;

import com.example.onlinestore.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, OrderStatus status, BigDecimal totalPrice, String shippingAddress,
                            String phoneNumber, List<OrderItemResponse> items, LocalDateTime createdAt) {

}