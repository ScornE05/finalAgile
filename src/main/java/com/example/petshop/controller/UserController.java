package com.example.petshop.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.petshop.entity.User;
import com.example.petshop.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "user/register";
        }

        // Kiểm tra username đã tồn tại chưa
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại!");
            return "user/register";
        }

        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email đã tồn tại!");
            return "user/register";
        }

        // Đặt role mặc định là CUSTOMER
        user.setRole("CUSTOMER");

        // Lưu user
        userService.save(user);

        return "redirect:/login?registered";
    }

    @GetMapping("/profile")
    public String viewProfile(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        return "user/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User user,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByUsername(principal.getName());

        // Cập nhật thông tin cá nhân
        currentUser.setFullName(user.getFullName());
        currentUser.setPhone(user.getPhone());
        currentUser.setAddress(user.getAddress());

        // Không cho phép thay đổi username, email và role
        // Lưu user
        userService.update(currentUser);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");

        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "user/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName());

        // Kiểm tra mật khẩu hiện tại
        if (!userService.isValidPassword(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "redirect:/profile/change-password";
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/profile/change-password";
        }

        // Cập nhật mật khẩu
        user.setPassword(userService.encodePassword(newPassword));
        userService.update(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");

        return "redirect:/profile";
    }
}
