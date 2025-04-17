package com.example.petshop.service;


import java.math.BigDecimal;
import java.util.List;

import com.example.petshop.entity.Cart;
import com.example.petshop.entity.CartItem;

public interface CartService {
    Cart getCartByUserId(Long userId);

    Cart createCart(Long userId);

    CartItem addProductToCart(Long cartId, Long productId, Integer quantity);

    CartItem addPetToCart(Long cartId, Long petId, Integer quantity);

    CartItem updateCartItem(Long cartItemId, Integer quantity);

    void removeCartItem(Long cartItemId);

    void clearCart(Long cartId);

    int getCartItemCount(Long cartId);

    BigDecimal getCartTotal(Long cartId);

    List<CartItem> getCartItems(Long cartId);
}
