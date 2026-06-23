package com.example.onlinestore.service;

import com.example.onlinestore.dto.CreateOrderRequest;
import com.example.onlinestore.dto.OrderItemResponse;
import com.example.onlinestore.dto.OrderResponse;
import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CartItem;
import com.example.onlinestore.entity.CustomerOrder;
import com.example.onlinestore.entity.OrderItem;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.CartItemRepository;
import com.example.onlinestore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CurrentUserService currentUserService;

    public OrderService(
            OrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            CurrentUserService currentUserService
    ) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public OrderResponse checkout(CreateOrderRequest request) {
        AppUser currentUser = currentUserService.getCurrentUser();

        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());

        BigDecimal orderTotalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (!product.getActive()) {
                throw new IllegalArgumentException("Товар недоступен: " + product.getName());
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Недостаточно товара на складе: " + product.getName());
            }

            BigDecimal itemTotalPrice = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(itemTotalPrice);

            order.addItem(orderItem);

            orderTotalPrice = orderTotalPrice.add(itemTotalPrice);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        }

        order.setTotalPrice(orderTotalPrice);

        CustomerOrder savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByUser(currentUser);

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {
        AppUser currentUser = currentUserService.getCurrentUser();

        return orderRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getMyOrderById(Long orderId) {
        AppUser currentUser = currentUserService.getCurrentUser();

        CustomerOrder order = orderRepository.findByIdAndUser(orderId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        return toResponse(order);
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getShippingAddress(),
                order.getPhoneNumber(),
                items,
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getQuantity(),
                item.getTotalPrice()
        );
    }
}