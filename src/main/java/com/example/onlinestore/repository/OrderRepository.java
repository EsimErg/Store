package com.example.onlinestore.repository;

import com.example.onlinestore.entity.AppUser;
import com.example.onlinestore.entity.CustomerOrder;
import com.example.onlinestore.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUserOrderByCreatedAtDesc(AppUser user);

    Optional<CustomerOrder> findByIdAndUser(Long id, AppUser user);

    List<CustomerOrder> findAllByOrderByCreatedAtDesc();

    List<CustomerOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}