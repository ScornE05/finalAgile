package com.example.petshop.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.petshop.entity.User;

public interface UserService {
    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    User save(User user);

    User update(User user);

    void delete(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByRole(String role, Pageable pageable);

    boolean isValidPassword(String rawPassword, String encodedPassword);

    String encodePassword(String password);
}