package com.example.petshop.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.petshop.entity.Pet;
import com.example.petshop.service.PetService;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public String getAllPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Pet> petPage;

        // Áp dụng các điều kiện lọc
        if (species != null && !species.isEmpty()) {
            petPage = petService.findBySpecies(species, pageable);
        } else if (minPrice != null && maxPrice != null) {
            petPage = petService.findByPriceRange(minPrice, maxPrice, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            petPage = petService.findByKeyword(keyword, pageable);
        } else {
            petPage = petService.findByStatus("AVAILABLE", pageable);
        }

        model.addAttribute("pets", petPage.getContent());
        model.addAttribute("currentPage", petPage.getNumber());
        model.addAttribute("totalPages", petPage.getTotalPages());
        model.addAttribute("totalItems", petPage.getTotalElements());

        model.addAttribute("species", species);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "pet/list";
    }

    @GetMapping("/{id}")
    public String getPetDetail(@PathVariable Long id, Model model) {
        Pet pet = petService.findById(id);

        // Lấy các thú cưng cùng loại
        Pageable pageable = PageRequest.of(0, 4);
        Page<Pet> relatedPets = petService.findBySpecies(pet.getSpecies(), pageable);

        model.addAttribute("pet", pet);
        model.addAttribute("relatedPets", relatedPets.getContent());

        return "pet/detail";
    }

    @GetMapping("/species/{species}")
    public String getPetsBySpecies(
            @PathVariable String species,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Pet> petPage = petService.findBySpecies(species, pageable);

        model.addAttribute("species", species);
        model.addAttribute("pets", petPage.getContent());
        model.addAttribute("currentPage", petPage.getNumber());
        model.addAttribute("totalPages", petPage.getTotalPages());
        model.addAttribute("totalItems", petPage.getTotalElements());

        return "pet/species";
    }
}