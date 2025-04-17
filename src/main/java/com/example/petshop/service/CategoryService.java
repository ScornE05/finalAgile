package com.example.petshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.petshop.entity.Category;

public interface CategoryService {
    Category findById(Long id);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    List<Category> findAllParentCategories();

    List<Category> findSubcategoriesByParentId(Long parentId);

    Category save(Category category);

    Category update(Category category);

    void delete(Long id);

    Long countProductsByCategoryId(Long categoryId);
}