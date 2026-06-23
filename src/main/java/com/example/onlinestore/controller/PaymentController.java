package com.example.onlinestore.controller;

import com.example.onlinestore.dto.PaymentRequest;
import com.example.onlinestore.dto.PaymentResponse;
import com.example.onlinestore.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Payments", description = "Оплата заказов")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> payOrder(
            @Valid @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = paymentService.payOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        List<PaymentResponse> response = paymentService.getMyPayments();
        return ResponseEntity.ok(response);
    }
}
