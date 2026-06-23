package com.example.onlinestore.controller;

import com.example.onlinestore.dto.CreateOrderRequest;
import com.example.onlinestore.dto.OrderResponse;
import com.example.onlinestore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Orders", description = "Заказы текущего пользователя")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<OrderResponse> response = orderService.getMyOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrderById(
            @PathVariable Long orderId
    ) {
        OrderResponse response = orderService.getMyOrderById(orderId);
        return ResponseEntity.ok(response);
    }
}