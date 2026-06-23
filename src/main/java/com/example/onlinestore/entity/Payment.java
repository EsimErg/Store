package com.example.onlinestore.entity;

import com.example.onlinestore.enums.PaymentMethod;
import com.example.onlinestore.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Один заказ имеет одну текущую оплату
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;

    // Кто оплатил
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Setter
    @Column(nullable = false)
    private BigDecimal amount;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Setter
    @Column(nullable = false, unique = true)
    private String transactionId;

    @Setter
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime paidAt;

    public Payment() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

}