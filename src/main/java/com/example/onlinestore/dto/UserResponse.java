package com.example.onlinestore.dto;

import com.example.onlinestore.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(Long id, String fullName, String email, Role role, Boolean enabled,
                           LocalDateTime createdAt) {

}