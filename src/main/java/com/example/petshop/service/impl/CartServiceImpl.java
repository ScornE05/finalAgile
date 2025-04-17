package com.example.petshop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.petshop.entity.Cart;
import com.example.petshop.entity.CartItem;
import com.example.petshop.entity.Pet;
import com.example.petshop.entity.Product;
import com.example.petshop.entity.User;
import com.example.petshop.exception.ResourceNotFoundException;
import com.example.petshop.repository.CartItemRepository;
import com.example.petshop.repository.CartRepository;
import com.example.petshop.repository.PetRepository;
import com.example.petshop.repository.ProductRepository;
import com.example.petshop.repository.UserRepository;
import com.example.petshop.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PetRepository petRepository;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    @Override
    @Transactional
    public Cart createCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));

        // Kiểm tra nếu giỏ hàng đã tồn tại
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Tạo giỏ hàng mới
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartItem addProductToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với id: " + cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id: " + productId));

        // Kiểm tra sản phẩm đã có trong giỏ hàng
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (existingItem.isPresent()) {
            // Cập nhật số lượng của mặt hàng hiện có
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Thêm mặt hàng mới vào giỏ
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setPet(null);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public CartItem addPetToCart(Long cartId, Long petId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với id: " + cartId));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thú cưng với id: " + petId));

        // Kiểm tra thú cưng đã có trong giỏ hàng
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndPetId(cartId, petId);

        if (existingItem.isPresent()) {
            // Cập nhật số lượng của mặt hàng hiện có
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Thêm thú cưng mới vào giỏ
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(null);
            cartItem.setPet(pet);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public CartItem updateCartItem(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mặt hàng trong giỏ với id: " + cartItemId));

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    @Override
    public int getCartItemCount(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }

    @Override
    public BigDecimal getCartTotal(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                BigDecimal itemPrice = item.getProduct().getDiscountPrice() != null
                        ? item.getProduct().getDiscountPrice()
                        : item.getProduct().getPrice();
                total = total.add(itemPrice.multiply(new BigDecimal(item.getQuantity())));
            } else if (item.getPet() != null) {
                total = total.add(item.getPet().getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }

        return total;
    }

    @Override
    public List<CartItem> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
}
