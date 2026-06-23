package com.example.onlinestore.dto;

import com.example.onlinestore.enums.PaymentMethod;
import com.example.onlinestore.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(Long id, Long orderId, BigDecimal amount, PaymentMethod method, PaymentStatus status,
                              String transactionId, String failureReason, LocalDateTime createdAt,
                              LocalDateTime paidAt) {

}