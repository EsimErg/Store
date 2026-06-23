package com.example.onlinestore.dto;

import com.example.onlinestore.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(Long id, Long userId, String customerName, String customerEmail, OrderStatus status,
                                 BigDecimal totalPrice, String shippingAddress, String phoneNumber,
                                 List<OrderItemResponse> items, LocalDateTime createdAt, LocalDateTime updatedAt) {

}