package com.example.onlinestore.controller;

import com.example.onlinestore.dto.AuthResponse;
import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.RegisterRequest;
import com.example.onlinestore.dto.UserResponse;
import com.example.onlinestore.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Auth", description = "Регистрация и логин пользователей")
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "Логин пользователя и получение JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
