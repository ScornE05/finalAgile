package com.example.petshop.service.impl;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.petshop.entity.Product;
import com.example.petshop.entity.ProductImage;
import com.example.petshop.exception.ResourceNotFoundException;
import com.example.petshop.repository.ProductImageRepository;
import com.example.petshop.repository.ProductRepository;
import com.example.petshop.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    @Override
    public Page<Product> findByStatus(String status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByKeyword(String keyword, Pageable pageable) {
        return productRepository.findByKeyword(keyword, pageable);
    }

    @Override
    public Page<Product> findByCategoryAndPriceRange(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                     Pageable pageable) {
        return productRepository.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product update(Product product) {
        Product existingProduct = findById(product.getId());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductImage addProductImage(Long productId, MultipartFile file, boolean isPrimary) {
        try {
            Product product = findById(productId);

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir + "/products");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Copy file to the upload directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // If this is a primary image, update all other images to non-primary
            if (isPrimary) {
                for (ProductImage image : product.getImages()) {
                    if (image.getIsPrimary()) {
                        image.setIsPrimary(false);
                        productImageRepository.save(image);
                    }
                }
            }

            // Create and save new product image
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl("/uploads/products/" + filename);
            productImage.setIsPrimary(isPrimary);

            return productImageRepository.save(productImage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    @Transactional
    public void deleteProductImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void updateProductStock(Long productId, int quantity) {
        Product product = findById(productId);
        product.setQuantityInStock(product.getQuantityInStock() + quantity);
        productRepository.save(product);
    }
}
