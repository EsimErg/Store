package com.example.onlinestore.service;

import com.example.onlinestore.dto.AuthResponse;
import com.example.onlinestore.dto.LoginRequest;
import com.example.onlinestore.dto.RegisterRequest;
import com.example.onlinestore.dto.UserResponse;
import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.enums.Role;
import com.example.onlinestore.exception.DuplicateResourceException;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.AppUserRepository;
import com.example.onlinestore.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Пользователь с таким email уже существует");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        AppUser savedUser = appUserRepository.save(user);

        return toResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                toResponse(user)
        );
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }
}