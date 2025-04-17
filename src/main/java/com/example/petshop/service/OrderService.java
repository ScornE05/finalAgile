package com.example.petshop.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.petshop.entity.Order;

public interface OrderService {
    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByStatus(String status);

    Page<Order> findByStatus(String status, Pageable pageable);

    List<Order> findAll();

    Page<Order> findAll(Pageable pageable);

    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Order save(Order order);

    Order createOrderFromCart(Long userId, String shippingAddress, String phone, String paymentMethod, String note);

    Order updateStatus(Long id, String status);

    void delete(Long id);

    Long countByStatus(String status);

    Double getTotalSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}