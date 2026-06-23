package com.example.onlinestore.dto;

public record AuthResponse(String token, String tokenType, Long expiresIn, UserResponse user) {

}