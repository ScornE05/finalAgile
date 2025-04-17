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

import com.example.petshop.entity.Pet;
import com.example.petshop.entity.PetImage;
import com.example.petshop.exception.ResourceNotFoundException;
import com.example.petshop.repository.PetImageRepository;
import com.example.petshop.repository.PetRepository;
import com.example.petshop.service.PetService;

@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetImageRepository petImageRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thú cưng với id: " + id));
    }

    @Override
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    @Override
    public Page<Pet> findAll(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    @Override
    public List<Pet> findByStatus(String status) {
        return petRepository.findByStatus(status);
    }

    @Override
    public Page<Pet> findByStatus(String status, Pageable pageable) {
        return petRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Pet> findBySpecies(String species) {
        return petRepository.findBySpecies(species);
    }

    @Override
    public Page<Pet> findBySpecies(String species, Pageable pageable) {
        return petRepository.findBySpecies(species, pageable);
    }

    @Override
    public Page<Pet> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return petRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Pet> findByKeyword(String keyword, Pageable pageable) {
        return petRepository.findByKeyword(keyword, pageable);
    }

    @Override
    @Transactional
    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    @Transactional
    public Pet update(Pet pet) {
        Pet existingPet = findById(pet.getId());
        return petRepository.save(pet);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        petRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PetImage addPetImage(Long petId, MultipartFile file, boolean isPrimary) {
        try {
            Pet pet = findById(petId);

            // Tạo thư mục upload nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir + "/pets");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Sao chép file vào thư mục upload
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Nếu là ảnh chính, cập nhật các ảnh khác thành không phải ảnh chính
            if (isPrimary) {
                for (PetImage image : pet.getImages()) {
                    if (image.getIsPrimary()) {
                        image.setIsPrimary(false);
                        petImageRepository.save(image);
                    }
                }
            }

            // Tạo và lưu thông tin ảnh mới
            PetImage petImage = new PetImage();
            petImage.setPet(pet);
            petImage.setImageUrl("/uploads/pets/" + filename);
            petImage.setIsPrimary(isPrimary);

            return petImageRepository.save(petImage);
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu trữ file", e);
        }
    }

    @Override
    @Transactional
    public void deletePetImage(Long imageId) {
        petImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void updatePetStatus(Long petId, String status) {
        Pet pet = findById(petId);
        pet.setStatus(status);
        petRepository.save(pet);
    }
}