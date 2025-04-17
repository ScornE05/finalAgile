package com.example.petshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.petshop.entity.Category;
import com.example.petshop.entity.Product;
import com.example.petshop.service.CategoryService;
import com.example.petshop.service.ProductService;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String getAllCategories(Model model) {
        List<Category> parentCategories = categoryService.findAllParentCategories();
        model.addAttribute("parentCategories", parentCategories);

        return "category/list";
    }

    @GetMapping("/{id}")
    public String getCategoryDetail(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);

        // Get subcategories if this is a parent category
        if (category.getParent() == null) {
            List<Category> subcategories = categoryService.findSubcategoriesByParentId(id);
            model.addAttribute("subcategories", subcategories);
        }

        model.addAttribute("category", category);

        return "category/detail";
    }
}