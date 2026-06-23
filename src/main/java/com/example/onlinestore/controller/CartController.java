package com.example.onlinestore.controller;

import com.example.onlinestore.dto.AddToCartRequest;
import com.example.onlinestore.dto.CartResponse;
import com.example.onlinestore.dto.UpdateCartItemRequest;
import com.example.onlinestore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Cart", description = "Корзина текущего пользователя")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        CartResponse response = cartService.getMyCart();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @Valid @RequestBody AddToCartRequest request
    ) {
        CartResponse response = cartService.addItemToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartResponse response = cartService.updateCartItem(itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable Long itemId
    ) {
        CartResponse response = cartService.removeCartItem(itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearMyCart() {
        cartService.clearMyCart();
        return ResponseEntity.noContent().build();
    }
}