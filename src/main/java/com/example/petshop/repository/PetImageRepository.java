package com.example.petshop.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.petshop.entity.PetImage;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {
    List<PetImage> findByPetId(Long petId);

    PetImage findByPetIdAndIsPrimaryTrue(Long petId);
}