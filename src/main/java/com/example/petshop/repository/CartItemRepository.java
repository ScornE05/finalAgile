package com.example.petshop.repository;

import java.util.List;
import java.util.Optional;

import com.example.petshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    Optional<CartItem> findByCartIdAndPetId(Long cartId, Long petId);

    void deleteByCartId(Long cartId);
}
