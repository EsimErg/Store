package com.example.onlinestore.service;

import com.example.onlinestore.dto.PaymentRequest;
import com.example.onlinestore.dto.PaymentResponse;
import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CustomerOrder;
import com.example.onlinestore.entity.Payment;
import com.example.onlinestore.enums.OrderStatus;
import com.example.onlinestore.enums.PaymentStatus;
import com.example.onlinestore.exception.ResourceNotFoundException;
import com.example.onlinestore.repository.OrderRepository;
import com.example.onlinestore.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            CurrentUserService currentUserService
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PaymentResponse payOrder(PaymentRequest request) {
        AppUser currentUser = currentUserService.getCurrentUser();

        CustomerOrder order = orderRepository.findByIdAndUser(
                        request.getOrderId(),
                        currentUser
                )
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Заказ уже оплачен");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Отменённый заказ нельзя оплатить");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElse(null);

        if (payment == null) {
            payment = new Payment();
            payment.setOrder(order);
            payment.setUser(currentUser);
            payment.setAmount(order.getTotalPrice());
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Оплата уже прошла успешно");
        }

        payment.setMethod(request.getMethod());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setAmount(order.getTotalPrice());

        if (request.getSuccess()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setFailureReason(null);
            payment.setPaidAt(LocalDateTime.now());

            order.setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Оплата отклонена платёжной системой");

            order.setStatus(OrderStatus.PENDING_PAYMENT);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    public List<PaymentResponse> getMyPayments() {
        AppUser currentUser = currentUserService.getCurrentUser();

        return paymentRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getPaidAt()
        );
    }
}