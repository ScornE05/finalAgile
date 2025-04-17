package com.example.petshop.controller.admin;

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

import com.example.petshop.entity.Order;
import com.example.petshop.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage;
        if (status != null && !status.isEmpty()) {
            orderPage = orderService.findByStatus(status, pageable);
        } else {
            orderPage = orderService.findAll(pageable);
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/order/list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);

        return "admin/order/detail";
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            orderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa đơn hàng: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/pending")
    public String viewPendingOrders(Model model) {
        return listOrders(0, 10, "PENDING", "createdAt", "desc", model);
    }

    @GetMapping("/processing")
    public String viewProcessingOrders(Model model) {
        return listOrders(0, 10, "PROCESSING", "createdAt", "desc", model);
    }

    @GetMapping("/shipped")
    public String viewShippedOrders(Model model) {
        return listOrders(0, 10, "SHIPPED", "createdAt", "desc", model);
    }

    @GetMapping("/delivered")
    public String viewDeliveredOrders(Model model) {
        return listOrders(0, 10, "DELIVERED", "createdAt", "desc", model);
    }

    @GetMapping("/cancelled")
    public String viewCancelledOrders(Model model) {
        return listOrders(0, 10, "CANCELLED", "createdAt", "desc", model);
    }
}
