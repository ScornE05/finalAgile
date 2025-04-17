package com.example.petshop.controller;


import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.petshop.entity.Cart;
import com.example.petshop.entity.CartItem;
import com.example.petshop.entity.User;
import com.example.petshop.service.CartService;
import com.example.petshop.service.UserService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getCartByUserId(user.getId());
        List<CartItem> cartItems = cartService.getCartItems(cart.getId());

        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(cart.getId()));
        model.addAttribute("itemCount", cartService.getCartItemCount(cart.getId()));

        return "cart/view";
    }

    @PostMapping("/add/product/{productId}")
    public String addProductToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getCartByUserId(user.getId());

        cartService.addProductToCart(cart.getId(), productId, quantity);

        redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm vào giỏ hàng!");

        return "redirect:/products/" + productId;
    }

    @PostMapping("/add/pet/{petId}")
    public String addPetToCart(
            @PathVariable Long petId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getCartByUserId(user.getId());

        // Thú cưng thường chỉ có số lượng là 1
        cartService.addPetToCart(cart.getId(), petId, 1);

        redirectAttributes.addFlashAttribute("success", "Thú cưng đã được thêm vào giỏ hàng!");

        return "redirect:/pets/" + petId;
    }

    @PostMapping("/update/{cartItemId}")
    public String updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.updateCartItem(cartItemId, quantity);

        return "redirect:/cart";
    }

    @GetMapping("/remove/{cartItemId}")
    public String removeCartItem(
            @PathVariable Long cartItemId,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        cartService.removeCartItem(cartItemId);

        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        Cart cart = cartService.getCartByUserId(user.getId());

        cartService.clearCart(cart.getId());

        return "redirect:/cart";
    }
}