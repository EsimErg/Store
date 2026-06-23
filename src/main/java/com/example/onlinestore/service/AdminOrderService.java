package com.example.onlinestore.service;

import com.example.onlinestore.dto.AdminOrderResponse;
import com.example.onlinestore.dto.OrderItemResponse;
import com.example.onlinestore.dto.UpdateOrderStatusRequest;
import com.example.onlinestore.entity.CustomerOrder;
import com.example.onlinestore.entity.OrderItem;
import com.example.onlinestore.entity.Product;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminOrderResponse getOrderById(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        return toAdminResponse(order);
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public AdminOrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        validateStatusChange(currentStatus, newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);

        CustomerOrder savedOrder = orderRepository.save(order);

        return toAdminResponse(savedOrder);
    }

    private void validateStatusChange(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Отменённый заказ нельзя изменить");
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Доставленный заказ нельзя изменить");
        }

        if (newStatus == OrderStatus.PENDING_PAYMENT) {
            throw new IllegalArgumentException("Нельзя вернуть заказ в статус ожидания оплаты");
        }

        if (newStatus == OrderStatus.PAID) {
            throw new IllegalArgumentException("Статус PAID должен ставиться только после оплаты");
        }

        if (currentStatus == OrderStatus.PENDING_PAYMENT
                && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Неоплаченный заказ можно только отменить");
        }

        if (currentStatus == OrderStatus.PAID
                && newStatus != OrderStatus.PROCESSING
                && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Оплаченный заказ можно перевести только в PROCESSING или CANCELLED");
        }

        if (currentStatus == OrderStatus.PROCESSING
                && newStatus != OrderStatus.SHIPPED
                && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Заказ в обработке можно перевести только в SHIPPED или CANCELLED");
        }

        if (currentStatus == OrderStatus.SHIPPED
                && newStatus != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Отправленный заказ можно перевести только в DELIVERED");
        }
    }

    private void restoreStock(CustomerOrder order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
    }

    private AdminOrderResponse toAdminResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new AdminOrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getShippingAddress(),
                order.getPhoneNumber(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
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