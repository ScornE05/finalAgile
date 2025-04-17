package com.example.petshop.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.petshop.entity.Category;
import com.example.petshop.entity.Pet;
import com.example.petshop.entity.Product;
import com.example.petshop.service.CategoryService;
import com.example.petshop.service.PetService;
import com.example.petshop.service.ProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PetService petService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"", "/", "/home"})
    public String home(Model model) {
        // Lấy danh sách sản phẩm nổi bật
        List<Product> featuredProducts = productService.findFeaturedProducts();
        model.addAttribute("featuredProducts", featuredProducts);

        // Lấy danh sách thú cưng có sẵn
        List<Pet> availablePets = petService.findByStatus("AVAILABLE");
        model.addAttribute("availablePets", availablePets);

        // Lấy danh sách danh mục chính
        List<Category> parentCategories = categoryService.findAllParentCategories();
        model.addAttribute("parentCategories", parentCategories);

        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
