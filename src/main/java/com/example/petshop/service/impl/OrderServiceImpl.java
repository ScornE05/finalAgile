package com.example.petshop.service.impl;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.petshop.entity.Cart;
import com.example.petshop.entity.CartItem;
import com.example.petshop.entity.Order;
import com.example.petshop.entity.OrderItem;
import com.example.petshop.entity.User;
import com.example.petshop.exception.ResourceNotFoundException;
import com.example.petshop.repository.OrderItemRepository;
import com.example.petshop.repository.OrderRepository;
import com.example.petshop.repository.UserRepository;
import com.example.petshop.service.CartService;
import com.example.petshop.service.OrderService;
import com.example.petshop.service.ProductService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với id: " + id));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Page<Order> findByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createOrderFromCart(Long userId, String shippingAddress, String phone, String paymentMethod, String note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + userId));

        Cart cart = cartService.getCartByUserId(userId);
        List<CartItem> cartItems = cartService.getCartItems(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPhone(phone);
        order.setStatus("PENDING");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PENDING");
        order.setNote(note);
        order.setTotalAmount(cartService.getCartTotal(cart.getId()));
        order.setOrderItems(new ArrayList<>());

        Order savedOrder = orderRepository.save(order);

        // Tạo chi tiết đơn hàng từ giỏ hàng
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setQuantity(cartItem.getQuantity());

            if (cartItem.getProduct() != null) {
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setPet(null);

                // Sử dụng giá khuyến mãi nếu có
                BigDecimal price = cartItem.getProduct().getDiscountPrice() != null
                        ? cartItem.getProduct().getDiscountPrice()
                        : cartItem.getProduct().getPrice();
                orderItem.setUnitPrice(price);

                // Cập nhật số lượng tồn kho
                productService.updateProductStock(cartItem.getProduct().getId(), -cartItem.getQuantity());
            } else if (cartItem.getPet() != null) {
                orderItem.setProduct(null);
                orderItem.setPet(cartItem.getPet());
                orderItem.setUnitPrice(cartItem.getPet().getPrice());

                // Cập nhật trạng thái thú cưng
                cartItem.getPet().setStatus("RESERVED");
            }

            orderItemRepository.save(orderItem);
        }

        // Xóa giỏ hàng sau khi đã đặt hàng
        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, String status) {
        Order order = findById(id);
        order.setStatus(status);

        // Nếu đơn hàng đã giao, cập nhật trạng thái thanh toán
        if ("DELIVERED".equals(status)) {
            order.setPaymentStatus("PAID");
        }

        // Nếu đơn hàng bị hủy, hoàn lại hàng vào kho
        if ("CANCELLED".equals(status)) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct() != null) {
                    productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
                } else if (item.getPet() != null) {
                    item.getPet().setStatus("AVAILABLE");
                }
            }

            // Cập nhật trạng thái thanh toán nếu đã thanh toán trước
            if ("PAID".equals(order.getPaymentStatus())) {
                order.setPaymentStatus("REFUNDED");
            }
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public Double getTotalSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalSalesByDateRange(startDate, endDate);
    }
}
