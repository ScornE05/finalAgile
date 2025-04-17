package com.example.petshop.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.petshop.entity.Pet;
import com.example.petshop.entity.PetImage;

public interface PetService {
    Pet findById(Long id);

    List<Pet> findAll();

    Page<Pet> findAll(Pageable pageable);

    List<Pet> findByStatus(String status);

    Page<Pet> findByStatus(String status, Pageable pageable);

    List<Pet> findBySpecies(String species);

    Page<Pet> findBySpecies(String species, Pageable pageable);

    Page<Pet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Pet> findByKeyword(String keyword, Pageable pageable);

    Pet save(Pet pet);

    Pet update(Pet pet);

    void delete(Long id);

    PetImage addPetImage(Long petId, MultipartFile file, boolean isPrimary);

    void deletePetImage(Long imageId);

    void updatePetStatus(Long petId, String status);
}