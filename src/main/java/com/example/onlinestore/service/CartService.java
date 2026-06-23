package com.example.onlinestore.service;

import com.example.onlinestore.dto.AddToCartRequest;
import com.example.onlinestore.dto.CartItemResponse;
import com.example.onlinestore.dto.CartResponse;
import com.example.onlinestore.dto.UpdateCartItemRequest;
import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CartItem;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            CurrentUserService currentUserService
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }

    public CartResponse getMyCart() {
        AppUser currentUser = currentUserService.getCurrentUser();

        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);

        return toCartResponse(cartItems);
    }

    @Transactional
    public CartResponse addItemToCart(AddToCartRequest request) {
        AppUser currentUser = currentUserService.getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Товар не найден"));

        if (!product.getActive()) {
            throw new ResourceNotFoundException("Товар недоступен");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }

        CartItem cartItem = cartItemRepository
                .findByUserAndProduct(currentUser, product)
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUser(currentUser);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Недостаточно товара на складе");
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return getMyCart();
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request) {
        AppUser currentUser = currentUserService.getCurrentUser();

        CartItem cartItem = cartItemRepository.findByIdAndUser(itemId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Товар в корзине не найден"));

        Product product = cartItem.getProduct();

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        return getMyCart();
    }

    @Transactional
    public CartResponse removeCartItem(Long itemId) {
        AppUser currentUser = currentUserService.getCurrentUser();

        CartItem cartItem = cartItemRepository.findByIdAndUser(itemId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Товар в корзине не найден"));

        cartItemRepository.delete(cartItem);

        return getMyCart();
    }

    @Transactional
    public void clearMyCart() {
        AppUser currentUser = currentUserService.getCurrentUser();

        cartItemRepository.deleteByUser(currentUser);
    }

    private CartResponse toCartResponse(List<CartItem> cartItems) {
        List<CartItemResponse> items = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();

        Integer totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                items,
                totalItems,
                totalPrice
        );
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();

        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                totalPrice
        );
    }
}