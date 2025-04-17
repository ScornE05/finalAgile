package com.example.petshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.petshop.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Integer countSoldProductByProductId(@Param("productId") Long productId);

    @Query("SELECT oi.product.id, SUM(oi.quantity) as soldQuantity FROM OrderItem oi WHERE oi.product IS NOT NULL GROUP BY oi.product.id ORDER BY soldQuantity DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.pet IS NOT NULL")
    Long countSoldPets();
}
