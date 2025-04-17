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

import com.example.petshop.entity.Category;
import com.example.petshop.service.CategoryService;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryService.findAll(pageable);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/category/list";
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", categoryService.findAllParentCategories());
        return "admin/category/add";
    }

    @PostMapping("/add")
    public String addCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            @RequestParam(required = false) Long parentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("parentCategories", categoryService.findAllParentCategories());
            return "admin/category/add";
        }

        // Set parent category if parentId is provided
        if (parentId != null) {
            Category parentCategory = categoryService.findById(parentId);
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }

        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");

        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);
        List<Category> parentCategories = categoryService.findAllParentCategories();

        // Remove current category from possible parents to avoid cyclic dependencies
        parentCategories.removeIf(c -> c.getId().equals(id));

        model.addAttribute("category", category);
        model.addAttribute("parentCategories", parentCategories);

        return "admin/category/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            @RequestParam(required = false) Long parentId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("parentCategories", categoryService.findAllParentCategories());
            return "admin/category/edit";
        }

        // Ensure we're not creating a circular reference
        if (parentId != null && parentId.equals(id)) {
            redirectAttributes.addFlashAttribute("error", "Danh mục không thể là danh mục cha của chính nó!");
            return "redirect:/admin/categories/edit/" + id;
        }

        // Set parent category if parentId is provided
        if (parentId != null) {
            Category parentCategory = categoryService.findById(parentId);
            category.setParent(parentCategory);
        } else {
            category.setParent(null);
        }

        categoryService.update(category);
        redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công!");

        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // Check if the category has subcategories
            List<Category> subcategories = categoryService.findSubcategoriesByParentId(id);
            if (!subcategories.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể xóa danh mục này vì có " + subcategories.size() + " danh mục con. Vui lòng xóa danh mục con trước.");
                return "redirect:/admin/categories";
            }

            // Check if the category has products
            Long productCount = categoryService.countProductsByCategoryId(id);
            if (productCount > 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể xóa danh mục này vì có " + productCount + " sản phẩm thuộc danh mục. Vui lòng xóa sản phẩm hoặc chuyển sang danh mục khác.");
                return "redirect:/admin/categories";
            }

            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }
}