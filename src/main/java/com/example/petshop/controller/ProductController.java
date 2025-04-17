package com.example.petshop.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.petshop.entity.Category;
import com.example.petshop.entity.Product;
import com.example.petshop.service.CategoryService;
import com.example.petshop.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        // Áp dụng các điều kiện lọc
        if (categoryId != null && minPrice != null && maxPrice != null) {
            productPage = productService.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable);
        } else if (categoryId != null) {
            productPage = productService.findByCategoryId(categoryId, pageable);
        } else if (minPrice != null && maxPrice != null) {
            productPage = productService.findByPriceRange(minPrice, maxPrice, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.findByKeyword(keyword, pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        // Lấy danh sách danh mục
        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        model.addAttribute("categories", categories);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "product/list";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        // Lấy các sản phẩm liên quan (cùng danh mục)
        Pageable pageable = PageRequest.of(0, 4);
        Page<Product> relatedProducts = productService.findByCategoryId(product.getCategory().getId(), pageable);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts.getContent());

        return "product/detail";
    }

    @GetMapping("/category/{categoryId}")
    public String getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Category category = categoryService.findById(categoryId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findByCategoryId(categoryId, pageable);

        model.addAttribute("category", category);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        return "product/category";
    }
}
