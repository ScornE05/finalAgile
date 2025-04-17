package com.example.petshop.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.petshop.entity.User;
import com.example.petshop.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage;
        if (role != null && !role.isEmpty()) {
            userPage = userService.findByRole(role, pageable);
        } else {
            userPage = userService.findAll(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("role", role);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/user/list";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/add";
    }

    @PostMapping("/add")
    public String addUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/user/add";
        }

        // Kiểm tra username đã tồn tại chưa
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại!");
            return "admin/user/add";
        }

        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email đã tồn tại!");
            return "admin/user/add";
        }

        // Lưu người dùng
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");

        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/user/edit";
        }

        // Lưu user (sử dụng update để giữ nguyên mật khẩu nếu không thay đổi)
        userService.update(user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");

        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa người dùng: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/reset-password/{id}")
    public String showResetPasswordForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/reset-password";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/admin/users/reset-password/" + id;
        }

        User user = userService.findById(id);
        user.setPassword(userService.encodePassword(newPassword));
        userService.update(user);

        redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công!");

        return "redirect:/admin/users";
    }

    @GetMapping("/customers")
    public String viewCustomers(Model model) {
        return listUsers(0, 10, "CUSTOMER", "id", "asc", model);
    }

    @GetMapping("/staff")
    public String viewStaff(Model model) {
        return listUsers(0, 10, "STAFF", "id", "asc", model);
    }

    @GetMapping("/admins")
    public String viewAdmins(Model model) {
        return listUsers(0, 10, "ADMIN", "id", "asc", model);
    }
}