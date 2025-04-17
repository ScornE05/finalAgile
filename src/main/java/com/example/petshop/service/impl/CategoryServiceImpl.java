package com.example.petshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.petshop.entity.Category;
import com.example.petshop.exception.ResourceNotFoundException;
import com.example.petshop.repository.CategoryRepository;
import com.example.petshop.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id: " + id));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> findAllParentCategories() {
        return categoryRepository.findAllParentCategories();
    }

    @Override
    public List<Category> findSubcategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Category category) {
        Category existingCategory = findById(category.getId());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Long countProductsByCategoryId(Long categoryId) {
        return categoryRepository.countProductsByCategoryId(categoryId);
    }
}