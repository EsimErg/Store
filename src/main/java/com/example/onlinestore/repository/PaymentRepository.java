package com.example.onlinestore.repository;

import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CustomerOrder;
import com.example.onlinestore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(CustomerOrder order);

    List<Payment> findByUserOrderByCreatedAtDesc(AppUser user);
}