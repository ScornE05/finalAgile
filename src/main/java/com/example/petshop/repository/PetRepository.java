package com.example.petshop.repository;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.petshop.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByStatus(String status);

    Page<Pet> findByStatus(String status, Pageable pageable);

    List<Pet> findBySpecies(String species);

    Page<Pet> findBySpecies(String species, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Pet> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.species LIKE %:keyword% OR p.breed LIKE %:keyword%")
    Page<Pet> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}