package com.example.onlinestore.repository;

import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CartItem;
import com.example.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(AppUser user);

    Optional<CartItem> findByUserAndProduct(AppUser user, Product product);

    Optional<CartItem> findByIdAndUser(Long id, AppUser user);

    void deleteByUser(AppUser user);
}