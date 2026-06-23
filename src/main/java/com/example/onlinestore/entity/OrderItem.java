package com.example.onlinestore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Много OrderItem относятся к одному заказу
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    // Храним связь с товаром
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Сохраняем snapshot данных товара на момент заказа
    @Setter
    @Column(nullable = false)
    private String productName;

    @Setter
    @Column(nullable = false)
    private BigDecimal productPrice;

    @Setter
    @Column(nullable = false)
    private Integer quantity;

    @Setter
    @Column(nullable = false)
    private BigDecimal totalPrice;

    public OrderItem() {
    }

}