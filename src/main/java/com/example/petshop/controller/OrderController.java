package com.example.petshop.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.petshop.entity.Cart;
import com.example.petshop.entity.Order;
import com.example.petshop.entity.User;
import com.example.petshop.service.CartService;
import com.example.petshop.service.OrderService;
import com.example.petshop.service.UserService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage = orderService.findByUserId(user.getId(), pageable);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());

        return "order/list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Order order = orderService.findById(id);

        // Kiểm tra xem đơn hàng có thuộc về người dùng hiện tại không
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);

        return "order/detail";
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getCartByUserId(user.getId());

        // Kiểm tra giỏ hàng có trống không
        if (cartService.getCartItemCount(cart.getId()) == 0) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartService.getCartItems(cart.getId()));
        model.addAttribute("cartTotal", cartService.getCartTotal(cart.getId()));
        model.addAttribute("user", user);

        return "order/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam String shippingAddress,
            @RequestParam String phone,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String note,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());

        try {
            Order order = orderService.createOrderFromCart(user.getId(), shippingAddress, phone, paymentMethod, note);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công! Mã đơn hàng của bạn là #" + order.getId());
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }
}