package com.example.petshop.controller.admin;


import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.petshop.entity.Order;
import com.example.petshop.service.OrderService;
import com.example.petshop.service.PetService;
import com.example.petshop.service.ProductService;
import com.example.petshop.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // Lấy số lượng đơn hàng đang chờ xử lý
        Long pendingOrders = orderService.countByStatus("PENDING");
        model.addAttribute("pendingOrders", pendingOrders);

        // Lấy doanh thu tháng hiện tại
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime lastDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth());
        Double monthlyRevenue = orderService.getTotalSalesByDateRange(firstDayOfMonth, lastDayOfMonth);
        model.addAttribute("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : 0.0);

        // Lấy danh sách đơn hàng gần đây
        List<Order> recentOrders = orderService.findByDateRange(
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now());
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard";
    }
}