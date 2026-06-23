package com.example.onlinestore.controller;

import com.example.onlinestore.dto.AdminOrderResponse;
import com.example.onlinestore.dto.UpdateOrderStatusRequest;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.service.AdminOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Admin Orders", description = "Админское управление заказами")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        List<AdminOrderResponse> response = adminOrderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(
            @PathVariable Long orderId
    ) {
        AdminOrderResponse response = adminOrderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdminOrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status
    ) {
        List<AdminOrderResponse> response = adminOrderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        AdminOrderResponse response = adminOrderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }
}