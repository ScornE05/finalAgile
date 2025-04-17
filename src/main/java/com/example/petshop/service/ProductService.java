package com.example.petshop.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.petshop.entity.Product;
import com.example.petshop.entity.ProductImage;

public interface ProductService {
    Product findById(Long id);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findFeaturedProducts();

    Page<Product> findByStatus(String status, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByKeyword(String keyword, Pageable pageable);

    Page<Product> findByCategoryAndPriceRange(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);

    ProductImage addProductImage(Long productId, MultipartFile file, boolean isPrimary);

    void deleteProductImage(Long imageId);

    void updateProductStock(Long productId, int quantity);
}